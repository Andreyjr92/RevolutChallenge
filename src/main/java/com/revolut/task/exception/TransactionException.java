package com.revolut.task.exception;

public class TransactionException extends ClientMessageException {

    private static final String ERROR_MESSAGE = "Unable to submit transaction";

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException() {
        super(ERROR_MESSAGE);
    }
}
