package com.revolut.task;

import com.revolut.task.dao.AccountDAO;
import com.revolut.task.dao.AllAccountsDAO;
import com.revolut.task.dao.UserDAO;
import com.revolut.task.exception.TransactionException;
import com.revolut.task.model.Account;
import com.revolut.task.model.User;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class AccountDAOTest extends BaseTest {

    private static User testUser1;
    private static User testUser2;
    private static User testUser3;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void loadUsers() {
        testUser1 = new User(5L, "Mario", "Fernandes");
        testUser2 = new User(6L, "Gonsalo", "Higuain");
        testUser3 = new User(7L, "Xavi", "Hernandes");
        new UserDAO(testUser1, connectionPool).submit();
        new UserDAO(testUser2, connectionPool).submit();
        new UserDAO(testUser3, connectionPool).submit();
        new AccountDAO(new Account(5L, testUser1, 55250.5, "RUB"), connectionPool).submitNew();
        new AccountDAO(new Account(6L, testUser2, 35000.5, "RUB"), connectionPool).submitNew();
        new AccountDAO(new Account(7L, testUser3, 80050D, "RUB"), connectionPool).submitNew();
    }

    @Test
    public void getById() {
        Optional<Account> accountOpt = new AccountDAO.Identified(2L, connectionPool).get();

        assertTrue(accountOpt.isPresent());
        Account account = accountOpt.get();

        Account expectedAccount = new Account(2L, new User(2L, "Zenedin", "Zidan"), 15000D, "RUB");
        assertEquals(expectedAccount, account);
    }

    @Test
    public void getByIdNotPresent() {
        Optional<Account> accountOpt = new AccountDAO.Identified(8L, connectionPool).get();

        assertFalse(accountOpt.isPresent());
    }

    @Test
    public void submit() {
        Optional<Account> existingAccount = new AccountDAO.Identified(1L, connectionPool).get();

        assertTrue(existingAccount.isPresent());
        assertEquals(new Long(1), existingAccount.get().getUser().getId());

        new AccountDAO(
                new Account(
                        1L,
                        testUser2,
                        1234D,
                        "RUB"),
                connectionPool).submit();

        Optional<Account> modifiedAccount = new AccountDAO.Identified(1L, connectionPool).get();

        assertTrue(modifiedAccount.isPresent());
        Account expectedAccount = new Account(1L, new User(6L, "Gonsalo", "Higuain"), 1234D, "RUB");
        assertEquals(expectedAccount, modifiedAccount.get());
    }

    @Test
    public void submitNew() {
        Account testAccount = new Account(null, testUser1, 12345D, "RUB");
        new AccountDAO(testAccount, connectionPool).submitNew();

        Optional<Account> accountOpt = new AccountDAO.Identified(8L, connectionPool).get();

        assertTrue(accountOpt.isPresent());
        Account accountDb = accountOpt.get();

        // for simplicity reason do not override equals & hashcode
        Account expectedAccount = new Account(8L, new User(5L, "Mario", "Fernandes"), 12345D, "RUB");
        assertEquals(expectedAccount, accountDb);
    }

    @Test
    public void deposit() {
        new AccountDAO(baseAccount2, connectionPool).deposit(100D, "RUB");

        Optional<Account> account = new AccountDAO.Identified(baseAccount2.getId(), connectionPool).get();

        assertTrue(account.isPresent());
        assertEquals(new Double(15100.0), account.get().getBalance());
    }

    @Test
    public void depositWrongCurrency() {
        thrown.expect(TransactionException.class);
        thrown.expectMessage("Unable to submit transaction." +
                " Account currency is RUB, your transaction currency is USD");

        new AccountDAO(baseAccount1, connectionPool).deposit(100D, "USD");
    }

    @Test
    public void depositNegativeAmount() {
        thrown.expect(TransactionException.class);
        thrown.expectMessage("Unable to deposit money, please check your transaction details");

        new AccountDAO(baseAccount1, connectionPool).deposit(-100D, "RUB");
    }

    @Test
    public void withdraw() {
        new AccountDAO(baseAccount1, connectionPool).withdraw(300D, "RUB");

        Optional<Account> account = new AccountDAO.Identified(baseAccount1.getId(), connectionPool).get();

        assertTrue(account.isPresent());
        assertEquals(new Double(9700.5), account.get().getBalance());
    }

    @Test
    public void withdrawWrongCurrency() {
        thrown.expect(TransactionException.class);
        thrown.expectMessage("Unable to submit transaction. Account currency is RUB, your transaction currency is EUR");

        new AccountDAO(baseAccount1, connectionPool).withdraw(5555D, "EUR");
    }

    @Test
    public void withdrawNegativeAmount() {
        thrown.expect(TransactionException.class);
        thrown.expectMessage("Unable to withdraw money, please check your transaction details");

        new AccountDAO(baseAccount1, connectionPool).withdraw(-2000D, "RUB");
    }

    @Test
    public void withdrawNotSufficientFunds() {
        thrown.expect(TransactionException.class);
        thrown.expectMessage("No sufficient funds, failed to manage transaction");

        new AccountDAO(baseAccount1, connectionPool).withdraw(124567D, "RUB");
    }

    @Test
    public void getAll() {
        Optional<List<Account>> accounts = new AllAccountsDAO(connectionPool).get();

        List<Account> expectedAccounts = Arrays.asList(
                new Account(1L, new User(1L, "Andreas", "Iniesta"), 10000.5, "RUB"),
                new Account(2L, new User(2L, "Zenedin", "Zidan"), 15000D, "RUB"),
                new Account(3L, new User(3L, "David", "Backhem"), 20000D, "RUB"),
                new Account(4L, new User(3L, "David", "Backhem"), 30000D, "RUB"),
                new Account(5L, testUser1, 55250.5, "RUB"),
                new Account(6L, testUser2, 35000.5, "RUB"),
                new Account(7L, testUser3, 80050D, "RUB")
        );
        assertEquals(expectedAccounts, accounts.get());
    }
}
