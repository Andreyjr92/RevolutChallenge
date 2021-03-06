package com.revolut.task;

import com.revolut.task.connection_pool.TransactionalBlockingConnectionPool;
import com.revolut.task.dao.AccountDAO;
import com.revolut.task.dao.StartUpDatabaseDAO;
import com.revolut.task.dao.UserDAO;
import com.revolut.task.model.Account;
import com.revolut.task.model.User;
import com.revolut.task.simulation.BankSimulation;
import com.revolut.task.simulation.TransactionSimulation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class MultiThreadTransactionSimulationTest {

    private static final int ACCOUNTS_MAX = 3;
    static TransactionalBlockingConnectionPool connectionPool;
    static AccountDAO baseAccount1;
    static AccountDAO baseAccount2;
    static AccountDAO baseAccount3;
    static AccountDAO baseAccount4;


    @BeforeClass
    public static void init() {
        connectionPool = TransactionalBlockingConnectionPool.getTestInstance();
        StartUpDatabaseDAO startUpDatabaseDAO = new StartUpDatabaseDAO(connectionPool);
        startUpDatabaseDAO.createDatabase();
        User user1 = new User(1L, "Andreas", "Iniesta");
        User user2 = new User(2L, "Zenedin", "Zidan");
        User user3 = new User(3L, "David", "Backhem");
        User user4 = new User(4L, "John", "Terry");
        new UserDAO(user1, connectionPool).submit();
        new UserDAO(user2, connectionPool).submit();
        new UserDAO(user3, connectionPool).submit();
        new UserDAO(user4, connectionPool).submit();
        baseAccount1 = new AccountDAO(new Account(1L, user1, 10000.5, "RUB"), connectionPool);
        baseAccount2 = new AccountDAO(new Account(2L, user2, 15000D, "RUB"), connectionPool);
        baseAccount3 = new AccountDAO(new Account(3L, user3, 20000D, "RUB"), connectionPool);
        baseAccount4 = new AccountDAO(new Account(4L, user3, 30000D, "RUB"), connectionPool);
        Stream.of(baseAccount1, baseAccount2, baseAccount3, baseAccount4).forEach(AccountDAO::submitNew);
    }

    @AfterClass
    public static void cleanDatabase() {
        new CleanDatabaseDAO(connectionPool).clear();
    }

    @Test
    public void test() throws InterruptedException {
        List<AccountDAO> accounts = Arrays.asList(baseAccount1, baseAccount2, baseAccount3, baseAccount4);
        BankSimulation bankSimulation = new BankSimulation(accounts);
        Thread thread = null;
        for (int i = 0; i <= ACCOUNTS_MAX; i++) {
            thread = new Thread(new TransactionSimulation(bankSimulation, i));
            thread.start();
        }
        thread.join();
        Double totalBalance = bankSimulation.getTotalBalance();
        assertEquals(new Double(75000.5), totalBalance);
    }
}
