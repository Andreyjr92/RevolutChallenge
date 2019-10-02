package com.revolut.task.dto;

public class TransactionDTO {
    private Long from;
    private Long to;
    private Double amount;
    private String currency;

    public TransactionDTO() {
    }

    public TransactionDTO(Long from, Long to, Double amount, String currency) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.currency = currency;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}