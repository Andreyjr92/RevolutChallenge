package com.revolut.task.exception;

public class AccountOwnerIsNotPresent extends ClientMessageException {

    private static final String ERROR_MESSAGE = "Account owner not found in database";

    public AccountOwnerIsNotPresent() {
        super(ERROR_MESSAGE);
    }
}
