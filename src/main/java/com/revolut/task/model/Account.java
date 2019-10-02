package com.revolut.task.model;

import com.revolut.task.dto.AccountDTO;

public class Account {
    protected Long id;
    protected User user;
    protected Double balance;
    protected String currency;

    public Account(Long id, User user, Double balance, String currency) {
        this.id = id;
        this.user = user;
        this.balance = balance;
        this.currency = currency;
    }

    public Account(AccountDTO accountDTO) {
        this.user = new User(accountDTO.getPersonId());
        this.balance = accountDTO.getAmount();
        this.currency = accountDTO.getCurrency();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Double getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }
}