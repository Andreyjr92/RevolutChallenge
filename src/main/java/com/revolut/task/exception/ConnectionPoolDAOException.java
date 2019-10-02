package com.revolut.task.exception;


public class ConnectionPoolDAOException extends ClientMessageException {

    private static final String ERROR_MESSAGE = "Connection pool exception";

    public ConnectionPoolDAOException() {
        super(ERROR_MESSAGE);
    }

    public ConnectionPoolDAOException(String clientMessage, Exception exception) {
        super(clientMessage, exception);
    }
}
