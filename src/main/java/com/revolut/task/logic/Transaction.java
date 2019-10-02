package com.revolut.task.logic;

import com.revolut.task.dao.AccountDAO;

/**
 * <p>Withdraws money from one account and deposits on another one</p>
 */

public class Transaction {

    private AccountDAO from;
    private AccountDAO to;
    private Double amount;
    private String currency;

    public Transaction(AccountDAO accountFrom, AccountDAO accountTo,
                       Double amount, String currency) {
        this.from = accountFrom;
        this.to = accountTo;
        this.amount = amount;
        this.currency = currency;
    }

    public void submit() {
        from.withdraw(amount, currency);
        to.deposit(amount, currency);
    }
}