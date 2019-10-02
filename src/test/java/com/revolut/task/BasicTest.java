package com.revolut.task;

import com.revolut.task.connection_pool.TransactionalBlockingConnectionPool;
import com.revolut.task.dao.AccountDAO;
import com.revolut.task.dao.StartUpDatabaseDAO;
import com.revolut.task.dao.UserDAO;
import com.revolut.task.model.Account;
import com.revolut.task.model.User;
import org.junit.BeforeClass;

import java.util.stream.Stream;

public class BasicTest {

    static TransactionalBlockingConnectionPool connectionPool;
    static AccountDAO account1;
    static AccountDAO account2;
    static AccountDAO account3;
    static AccountDAO account4;

    @BeforeClass
    public static void init() {
        connectionPool = new TransactionalBlockingConnectionPool(3, "db_connection_test.properties");
        StartUpDatabaseDAO startUpDatabaseDAO = new StartUpDatabaseDAO(connectionPool);
        startUpDatabaseDAO.createDatabase();
        User user1 = new User(1L, "Andreas", "Iniesta");
        User user2 = new User(2L, "Zenedin", "Zidan");
        User user3 = new User(3L, "David", "Backhem");
        User user4 = new User(3L, "John", "Terry");
        new UserDAO(user1, connectionPool).submit();
        new UserDAO(user2, connectionPool).submit();
        new UserDAO(user3, connectionPool).submit();
        new UserDAO(user4, connectionPool).submit();
        account1 = new AccountDAO(new Account(1L, user1, 10000.5, "RUB"), connectionPool);
        account2 = new AccountDAO(new Account(2L, user2, 15000D, "RUB"), connectionPool);
        account3 = new AccountDAO(new Account(3L, user3, 20000D, "RUB"), connectionPool);
        account4 = new AccountDAO(new Account(4L, user3, 30000D, "RUB"), connectionPool);
        Stream.of(account1, account2, account3, account4).forEach(AccountDAO::submitNew);
    }
}