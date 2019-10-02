package com.revolut.task.exception;

public class ClientMessageException extends RuntimeException {

    private final String message;

    protected ClientMessageException(String message) {
        super(message);
        this.message = message;
    }

    public ClientMessageException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}