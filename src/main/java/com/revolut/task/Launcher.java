package com.revolut.task;

import com.revolut.task.config.JettyServer;
import com.revolut.task.connection_pool.TransactionalBlockingConnectionPool;
import com.revolut.task.dao.StartUpDatabaseDAO;
import org.eclipse.jetty.server.Server;

/**
 * <p>Launches Jetty server and initializes in memory database</p>
 */
public class Launcher {

    private final TransactionalBlockingConnectionPool connectionPool = new TransactionalBlockingConnectionPool();

    public static void main(String[] args) {
        try {
            new Launcher().start();
        } catch (Exception e) {
            System.out.println("Failed to start jetty server");
            e.printStackTrace();
        }
    }

    private void start() throws Exception {
        new StartUpDatabaseDAO(connectionPool).createDatabase();
        Server jettyServer = new JettyServer().get();
        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }
}