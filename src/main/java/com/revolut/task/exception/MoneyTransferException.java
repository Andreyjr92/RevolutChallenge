package com.revolut.task.exception;

public class MoneyTransferException extends ClientMessageException {

    private static final String ERROR_MESSAGE = "Failed to execute funds transfer";

    public MoneyTransferException() {
        super(ERROR_MESSAGE);
    }
}
