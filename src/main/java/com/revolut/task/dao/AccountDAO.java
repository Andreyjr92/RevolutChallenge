package com.revolut.task.dao;

import com.revolut.task.connection_pool.TransactionalBlockingConnectionPool;
import com.revolut.task.exception.AccountOwnerIsNotPresent;
import com.revolut.task.exception.TransactionException;
import com.revolut.task.model.Account;
import com.revolut.task.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>DAO class, brings {@link Account} to database.
 * Withdraw and deposit methods implemented to work in multithreaded environment</p>
 */
public class AccountDAO extends Account {

    private static final String INSERT_QUERY = "INSERT INTO ACCOUNT(PERSON_ID, BALANCE, CURRENCY) " +
            "VALUES (?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE ACCOUNT SET PERSON_ID = ?, BALANCE = ?, CURRENCY = ? WHERE ID = ?";
    private final Lock amountChangeLock = new ReentrantLock();
    private final Condition sufficientFunds = amountChangeLock.newCondition();
    private final TransactionalBlockingConnectionPool pool;

    public AccountDAO(Account account, TransactionalBlockingConnectionPool pool) {
        super(account.getId(), account.getUser(), account.getBalance(), account.getCurrency());
        this.pool = pool;
    }

    public void withdraw(Double amount, String currency) {
        amountChangeLock.lock();
        try {
            checkAccountCurrency(currency);
            if (balance == null || amount < 0) {
                throw new TransactionException("Unable to withdraw money, please check your transaction details");
            }
            while (balance < amount) {
                sufficientFunds.await(5, TimeUnit.SECONDS);
                if (balance < amount) {
                    throw new TransactionException("No sufficient funds, failed to manage transaction");
                }
            }
            balance -= amount;
            submit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            amountChangeLock.unlock();
        }
    }

    public void deposit(Double amount, String currency) {
        amountChangeLock.lock();
        try {
            checkAccountCurrency(currency);
            if (balance == null || amount < 0) {
                throw new TransactionException("Unable to deposit money, please check your transaction details");
            }
            balance += amount;
            submit();
            sufficientFunds.signal();
        } finally {
            amountChangeLock.unlock();
        }
    }

    private void checkAccountCurrency(String currency) {
        if (!this.currency.equalsIgnoreCase(currency)) {
            throw new TransactionException("Unable to submit transaction. Account currency is " +
                    this.currency + ", your transaction currency is " + currency);
        }
    }

    public void submitNew() {
        Connection connection = pool.getConnection();
        try (PreparedStatement insertAccountPS = connection.prepareStatement(INSERT_QUERY)) {
            setParametersToNewEntry(insertAccountPS);
            insertAccountPS.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.putConnectionBack(connection);
        }
    }

    public void submit() {
        if (id != null) {
            Connection connection = pool.getConnection();
            try (PreparedStatement updateBalancePS = connection.prepareStatement(UPDATE_QUERY)) {
                setParametersToExistingEntry(updateBalancePS);
                updateBalancePS.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                pool.putConnectionBack(connection);
            }
        }
    }

    private void setParametersToNewEntry(PreparedStatement updateBalancePS) throws SQLException {
        Long personId = user.getId();
        updateBalancePS.setLong(1, personId);
        updateBalancePS.setDouble(2, balance);
        updateBalancePS.setString(3, currency);
    }

    private void setParametersToExistingEntry(PreparedStatement updateBalancePS) throws SQLException {
        updateBalancePS.setLong(1, user.getId());
        updateBalancePS.setDouble(2, balance);
        updateBalancePS.setString(3, currency);
        updateBalancePS.setLong(4, id);
    }

    /**
     * <p>Identified account. Allows to obtain instance of {@link Account} by known id</p>
     */
    public static class Identified {

        private static final String SELECT_QUERY = "SELECT * FROM ACCOUNT WHERE ID = ?";
        private static final String DELETE_QUERY = "DELETE FROM ACCOUNT WHERE ID = ?";
        private final Long accountId;
        private final TransactionalBlockingConnectionPool pool;

        public Identified(Long accountId, TransactionalBlockingConnectionPool pool) {
            this.accountId = accountId;
            this.pool = pool;
        }

        public boolean delete() {
            if (accountId == null) {
                return false;
            }
            Connection connection = pool.getConnection();
            try (PreparedStatement deleteAccountPs = connection.prepareStatement(DELETE_QUERY)) {
                deleteAccountPs.setLong(1, accountId);
                deleteAccountPs.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                pool.putConnectionBack(connection);
            }
            return false;
        }

        public Optional<Account> get() {
            if (accountId == null) {
                return Optional.empty();
            }
            Connection connection = pool.getConnection();
            try (PreparedStatement selectAccountPS = connection.prepareStatement(SELECT_QUERY)) {
                selectAccountPS.setLong(1, accountId);
                ResultSet resultSet = selectAccountPS.executeQuery();
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                long id = resultSet.getLong("id");
                long personId = resultSet.getInt("person_id");
                Optional<User> accountOwnerOpt = new UserDAO.Identified(personId, pool).get();
                double balance = resultSet.getDouble("balance");
                String currency = resultSet.getString("currency");
                if (!accountOwnerOpt.isPresent()) {
                    throw new AccountOwnerIsNotPresent();
                }
                User accountOwner = accountOwnerOpt.get();
                Account account = new Account(id, accountOwner, balance, currency);
                return Optional.of(account);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                pool.putConnectionBack(connection);
            }
            return Optional.empty();
        }
    }
}