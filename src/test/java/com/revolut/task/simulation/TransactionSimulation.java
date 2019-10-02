package com.revolut.task.simulation;

public class TransactionSimulation implements Runnable {

    private final static Long MAX_ACCOUNT_ID = 3L;
    private final static Long MAX_AMOUNT = 10L;
    private final static Long TRANSACTIONS_LIMIT = 1000L;
    private final BankSimulation bankSimulation;
    private int accountFrom;

    public TransactionSimulation(BankSimulation bankSimulation, int accountFrom) {
        this.bankSimulation = bankSimulation;
        this.accountFrom = accountFrom;
    }

    @Override
    public void run() {
        int i = 0;
        while (i < TRANSACTIONS_LIMIT) {
            int accountTo = (int) (Math.random() * MAX_ACCOUNT_ID);

            if (accountTo == accountFrom) continue;

            int amount = (int) (Math.random() * MAX_AMOUNT);

            if (amount == 0) continue;
            System.out.println("Transfer amount " + amount + " from " + accountFrom + " to " + accountTo);
            bankSimulation.transact(accountFrom, accountTo, amount);
            i++;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
