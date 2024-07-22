package com.microservice.wallet.Models;

import jakarta.persistence.*;

@Entity
public class WalletDB {
    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_id_generator")
    //@SequenceGenerator(name="wallet_id_generator",initialValue= 1 ,allocationSize = 1)
    private Integer user_id;
    private Integer balance;

    public WalletDB() {
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public WalletDB(Integer user_id, Integer balance) {
        this.user_id = user_id;
        this.balance = balance;
    }
}
