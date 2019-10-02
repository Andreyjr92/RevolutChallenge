package com.revolut.task.simulation;

import com.revolut.task.dao.AccountDAO;
import com.revolut.task.logic.Transaction;
import com.revolut.task.model.Account;

import java.util.List;

public class BankSimulation {

    private final List<AccountDAO> accounts;

    public BankSimulation(List<AccountDAO> accounts) {
        this.accounts = accounts;
    }

    public Double getTotalBalance() {
        return accounts.stream()
                .map(Account::getBalance)
                .reduce(0D, Double::sum);
    }

    public void transact(int from, int to, double amount) {
        AccountDAO accountFrom = accounts.get(from);
        AccountDAO accountTo = accounts.get(to);
        new Transaction(accountFrom, accountTo, amount, "RUB").submit();
    }
}
