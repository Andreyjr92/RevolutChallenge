package com.revolut.task.config;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class JettyServer {

    private static final String JERSEY_SERVLET_NAME = "jersey-container-servlet";
    private static final String ROOT_PASS = "/api/*";
    private static final String DEFAULT_APP_PORT = "8080";

    public Server get() {
        String defaultAppPort = System.getenv("PORT");
        if (defaultAppPort == null || defaultAppPort.isEmpty()) {
            defaultAppPort = DEFAULT_APP_PORT;
        }
        Server jettyServer = new Server(Integer.parseInt(defaultAppPort));
        ServletContextHandler contextHandler = new ServletContextHandler(jettyServer, "/");
        ServletHolder servletHolder = new ServletHolder(
                JERSEY_SERVLET_NAME,
                new ServletContainer(
                        new JerseyConfiguration()
                )
        );
        contextHandler.addServlet(servletHolder, ROOT_PASS);
        return jettyServer;
    }
}