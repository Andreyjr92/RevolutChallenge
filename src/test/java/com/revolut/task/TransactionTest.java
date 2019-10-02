package com.revolut.task;

import com.revolut.task.dao.AccountDAO;
import com.revolut.task.exception.TransactionException;
import com.revolut.task.logic.Transaction;
import com.revolut.task.model.Account;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class TransactionTest extends BaseTest {

    @Test
    @Ignore
    public void regularTransaction() {
        new Transaction(baseAccount2, baseAccount1, 50D, "RUB").submit();

        Optional<Account> accountDb1 = new AccountDAO.Identified(baseAccount1.getId(), connectionPool).get();
        Optional<Account> accountDb2 = new AccountDAO.Identified(baseAccount2.getId(), connectionPool).get();

        assertEquals(accountDb1.get().getBalance(), new Double(10050.5D));
        assertEquals(accountDb2.get().getBalance(), new Double(14950.0));
    }

    @Test(expected = TransactionException.class)
    public void transactionExceedBalance() {
        new Transaction(baseAccount2, baseAccount1, 123456D, "RUB").submit();
    }

    @Test(expected = TransactionException.class)
    public void transactionNegative() {
        new Transaction(baseAccount2, baseAccount1, -123456D, "RUB").submit();
    }
}
