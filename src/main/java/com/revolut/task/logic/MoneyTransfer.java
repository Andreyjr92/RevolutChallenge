package com.revolut.task.logic;

import com.revolut.task.connection_pool.TransactionalBlockingConnectionPool;
import com.revolut.task.dao.AccountDAO;
import com.revolut.task.dto.TransactionDTO;
import com.revolut.task.exception.MoneyTransferException;
import com.revolut.task.model.Account;

import java.util.Optional;

/**
 * <p>Provides logic for money transitions between two accounts</p>
 */
public class MoneyTransfer {

    private final TransactionDTO transactionDTO;
    private final TransactionalBlockingConnectionPool pool;

    public MoneyTransfer(TransactionDTO transactionDTO, TransactionalBlockingConnectionPool pool) {
        this.transactionDTO = transactionDTO;
        this.pool = pool;
    }

    public void transfer() {
        Optional<Account> accountFromOpt = new AccountDAO.Identified(transactionDTO.getFrom(), pool).get();
        Optional<Account> accountToOpt = new AccountDAO.Identified(transactionDTO.getTo(), pool).get();
        if (!accountFromOpt.isPresent() || !accountToOpt.isPresent()) {
            throw new MoneyTransferException();
        }
        new Transaction(
                new AccountDAO(accountFromOpt.get(), pool),
                new AccountDAO(accountToOpt.get(), pool),
                transactionDTO.getAmount(),
                transactionDTO.getCurrency()
        ).submit();
    }
}