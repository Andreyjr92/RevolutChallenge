package com.revolut.task.connection_pool;

import com.revolut.task.exception.ConnectionPoolDAOException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Semaphore;

public class TransactionalBlockingConnectionPool {
    private static final String DEFAULT_DATA_SOURCE = "db_connection.properties";
    private static final int DEFAULT_SIZE = 20;

    private Properties prop;
    private Semaphore semaphore;
    private ThreadLocal<Connection> threadLocal;
    private int size;

    public TransactionalBlockingConnectionPool() {
        this.size = DEFAULT_SIZE;
        this.semaphore = new Semaphore(DEFAULT_SIZE);
        this.threadLocal = new ThreadLocal<>();
        this.prop = new Properties();
        try {
            this.prop.load(getReader(DEFAULT_DATA_SOURCE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TransactionalBlockingConnectionPool(int poolSize, String propertiesFilePath) {
        this.size = poolSize;
        this.semaphore = new Semaphore(poolSize);
        this.threadLocal = new ThreadLocal<>();
        this.prop = new Properties();
        try {
            this.prop.load(getReader(propertiesFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStreamReader getReader(String propertiesFilePath) {
        return new InputStreamReader(
                Objects.requireNonNull(TransactionalBlockingConnectionPool.class.getClassLoader()
                        .getResourceAsStream(propertiesFilePath)));
    }

    public Connection getConnection() {
        try {
            semaphore.acquire();
            Connection connection = threadLocal.get();
            threadLocal.remove();
            if (connection == null) {
                connection = getDBConnection();
                connection.setAutoCommit(true);
            }
            return connection;
        } catch (InterruptedException e) {
            throw new ConnectionPoolDAOException("Concurrent exception while acquiring connection", e);
        } catch (SQLException e) {
            throw new ConnectionPoolDAOException("Unable to set Auto Commit", e);
        }
    }

    public void putConnectionBack(Connection connectionBack) {
        threadLocal.set(connectionBack);
        semaphore.release();
    }

    public int getFreeConnections() {
        return semaphore.availablePermits();
    }

    public void commitConnection() {
        Connection connection = threadLocal.get();
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new ConnectionPoolDAOException("Unable to commit connection", e);
        } finally {
            closeConnection(connection);
        }
    }

    public void rollBackConnection() {
        Connection connection = threadLocal.get();
        try {
            connection.rollback();
        } catch (SQLException | NullPointerException e) {
            throw new ConnectionPoolDAOException("Unable to rollBack connection", e);
        } finally {
            closeConnection(connection);
        }
    }

    public int getActiveConnections() {
        return size - semaphore.availablePermits();
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new ConnectionPoolDAOException("Unable to close connection", e);
        } finally {
            threadLocal.remove();
            semaphore.release();
        }
    }

    private Connection getDBConnection() {
        try {
            String dbDriver = prop.getProperty("h2.database.driver");
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            throw new ConnectionPoolDAOException("Failed to obtain driver for H2 database", e);
        }
        try {
            String url = prop.getProperty("h2.database.url");
            return DriverManager.getConnection(url, prop);
        } catch (SQLException e) {
            throw new ConnectionPoolDAOException("Failed to create new connection to H2 database", e);
        }
    }
}