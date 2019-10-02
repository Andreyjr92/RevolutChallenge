package com.revolut.task;

import com.revolut.task.dao.AccountDAO;
import com.revolut.task.exception.TransactionException;
import com.revolut.task.logic.Transaction;
import com.revolut.task.model.Account;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class TransactionTest extends BasicTest {

    @Test
    public void regularTransaction() {
        new Transaction(account2, account1, 50D, "RUB").submit();

        Optional<Account> accountDb1 = new AccountDAO.Identified(account1.getId(), connectionPool).get();
        Optional<Account> accountDb2 = new AccountDAO.Identified(account2.getId(), connectionPool).get();

        assertEquals(accountDb1.get().getBalance(), new Double(10050.5D));
        assertEquals(accountDb2.get().getBalance(), new Double(14950.0));
    }

    @Test(expected = TransactionException.class)
    public void transactionExceedBalance() {
        new Transaction(account2, account1, 123456D, "RUB").submit();
    }

    @Test(expected = TransactionException.class)
    public void transactionNegative() {
        new Transaction(account2, account1, -123456D, "RUB").submit();
    }
}
