package com.revolut.task.exception;

public class UserNotFoundException extends ClientMessageException {

    private static final String ERROR_MESSAGE = "User not found in database";

    public UserNotFoundException() {
        super(ERROR_MESSAGE);
    }
}