package com.revolut.task;

import com.revolut.task.connection_pool.TransactionalBlockingConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CleanDatabaseDAO {
    private static final String TRUNCATE_ACCOUNT = "TRUNCATE TABLE ACCOUNT";
    private final TransactionalBlockingConnectionPool connectionPool;

    public CleanDatabaseDAO(TransactionalBlockingConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void clear() {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement truncateAccountPs = connection.prepareStatement(TRUNCATE_ACCOUNT)) {
            truncateAccountPs.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.putConnectionBack(connection);
        }
    }
}
