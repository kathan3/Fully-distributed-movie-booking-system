package com.microservice.wallet.Repository;

import com.microservice.wallet.Models.WalletDB;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation= Isolation.SERIALIZABLE)
public interface WalletDBRepository extends Repository <WalletDB,Integer> {
    WalletDB save(WalletDB wallet);
    // We can run into some issues if the field has an underscore, and we're trying to find or delete using built in spring functions.
    // To avoid them, below are custom SQL queries that perform the equivalent task.
    @Query("SELECT w FROM WalletDB w WHERE w.user_id = :user_id")
    WalletDB _findByUser_id(Integer user_id);
    @Modifying
    @Query("DELETE FROM WalletDB w WHERE w.user_id = :user_id")
    void _deleteByUser_id(Integer user_id);
    void deleteAll();
}
