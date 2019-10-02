package com.revolut.task.dao;

import com.revolut.task.connection_pool.TransactionalBlockingConnectionPool;
import com.revolut.task.exception.AccountOwnerIsNotPresent;
import com.revolut.task.model.Account;
import com.revolut.task.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AllAccountsDAO {

    private static final String SELECT_ALL = "SELECT * FROM ACCOUNT";
    private final TransactionalBlockingConnectionPool pool;

    public AllAccountsDAO(TransactionalBlockingConnectionPool pool) {
        this.pool = pool;
    }

    public Optional<List<Account>> get() {
        Connection connection = pool.getConnection();
        try (PreparedStatement selectAccountPS = connection.prepareStatement(SELECT_ALL)) {
            selectAccountPS.executeQuery();
            ResultSet resultSet = selectAccountPS.executeQuery();
            List<Account> accounts = new ArrayList<>();
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                long personId = resultSet.getInt("person_id");
                Optional<User> accountOwnerOpt = new UserDAO.Identified(personId, pool).get();
                double balance = resultSet.getDouble("balance");
                String currency = resultSet.getString("currency");
                if (!accountOwnerOpt.isPresent()) {
                    throw new AccountOwnerIsNotPresent();
                }
                User accountOwner = accountOwnerOpt.get();
                accounts.add(new Account(id, accountOwner, balance, currency));
            }
            return Optional.of(accounts);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.putConnectionBack(connection);
        }
        return Optional.empty();
    }
}