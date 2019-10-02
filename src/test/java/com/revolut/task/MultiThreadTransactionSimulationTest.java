package com.revolut.task;

import com.revolut.task.dao.AccountDAO;
import com.revolut.task.simulation.BankSimulation;
import com.revolut.task.simulation.TransactionSimulation;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MultiThreadTransactionSimulationTest extends BasicTest {

    private static final int ACCOUNTS_MAX = 3;

    @Test
    public void test() throws InterruptedException {
        List<AccountDAO> accounts = Arrays.asList(account1, account2, account3, account4);
        BankSimulation bankSimulation = new BankSimulation(accounts);
        Thread thread = null;
        for (int i = 0; i <= ACCOUNTS_MAX; i++) {
            thread = new Thread(new TransactionSimulation(bankSimulation, i));
            thread.start();
        }
        thread.join();
        Double totalBalance = bankSimulation.getTotalBalance();
        assertEquals(new Double(75000.5), totalBalance);
    }
}
