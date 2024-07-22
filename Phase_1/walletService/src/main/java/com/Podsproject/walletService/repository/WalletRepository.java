package com.Podsproject.walletService.repository;

import com.Podsproject.walletService.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Wallet findByUserId(Integer userId);
}
