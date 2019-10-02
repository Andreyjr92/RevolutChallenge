package com.revolut.task.dao;

import com.revolut.task.connection_pool.TransactionalBlockingConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * <p>Creates database schema and tables on application start up</p>
 */
public class StartUpDatabaseDAO {

    private static final String CREATE_PERSON = "CREATE TABLE IF NOT EXISTS PERSON(ID int auto_increment primary key," +
            " NAME varchar(100), SURNAME varchar(100))";
    private static final String CREATE_ACCOUNT = "CREATE TABLE IF NOT EXISTS ACCOUNT(ID int auto_increment primary key, " +
            "PERSON_ID int, BALANCE numeric(8,2), CURRENCY varchar(100),foreign key (PERSON_ID) references PERSON)";
    private final TransactionalBlockingConnectionPool pool;

    public StartUpDatabaseDAO(TransactionalBlockingConnectionPool pool) {
        this.pool = pool;
    }

    public void createDatabase() {
        System.out.println("Start creating database");
        Connection connection = pool.getConnection();
        try (PreparedStatement createPersonPreparedStatement = connection.prepareStatement(CREATE_PERSON);
             PreparedStatement createAccountPreparedStatement = connection.prepareStatement(CREATE_ACCOUNT)) {
            createPersonPreparedStatement.executeUpdate();
            createAccountPreparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.putConnectionBack(connection);
        }
        System.out.println("Database initialized");
    }
}
