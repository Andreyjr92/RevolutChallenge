package com.revolut.task.model;

import com.revolut.task.dto.AccountDTO;

import java.util.StringJoiner;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (id != null ? !id.equals(account.id) : account.id != null) return false;
        if (user != null ? !user.equals(account.user) : account.user != null) return false;
        if (balance != null ? !balance.equals(account.balance) : account.balance != null) return false;
        return currency != null ? currency.equals(account.currency) : account.currency == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Account.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("user=" + user)
                .add("balance=" + balance)
                .add("currency='" + currency + "'")
                .toString();
    }
}