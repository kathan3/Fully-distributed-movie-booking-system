package com.microservice.wallet.Models;

// A dummy class used when PUT /wallets/{user_id} endpoint is envoked
public class PayloadDB {
    String action;
    Integer amount;

    public PayloadDB() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public PayloadDB(String action, Integer amount) {
        this.action = action;
        this.amount = amount;
    }
}
