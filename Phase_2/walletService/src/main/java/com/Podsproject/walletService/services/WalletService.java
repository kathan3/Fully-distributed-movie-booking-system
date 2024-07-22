package com.Podsproject.walletService.services;


import com.Podsproject.walletService.DTO.*;
import com.Podsproject.walletService.entity.Wallet;
import com.Podsproject.walletService.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    public Wallet getWalletByUserId(Integer userId) {
        return walletRepository.findByUserId(userId);
    }

    public Wallet getOrCreateWallet(Integer userId) {
        Wallet wallet = walletRepository.findByUserId(userId);
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setBalance(0);
            wallet = walletRepository.save(wallet);
        }
        return wallet;
    }

    public int debitAmount(Wallet wallet, int amount) {
        int currentBalance = wallet.getBalance();
        if (currentBalance < amount) {
            return -1; // Insufficient balance
        }
        int updatedBalance = currentBalance - amount;
        wallet.setBalance(updatedBalance);
        walletRepository.save(wallet);
        return updatedBalance;
    }

    public int creditAmount(Wallet wallet, int amount) {
        int currentBalance = wallet.getBalance();
        int updatedBalance = currentBalance + amount;
        wallet.setBalance(updatedBalance);
        walletRepository.save(wallet);
        return updatedBalance;
    }
    public boolean deleteWallet(Integer userId) {
        Wallet wallet = walletRepository.findByUserId(userId);
        if (wallet != null) {
            walletRepository.delete(wallet);
            return true; // Wallet deleted successfully
        } else {
            return false; // Wallet not found
        }
    }
    public void deleteAllWallets() {
        walletRepository.deleteAll();
    }
}
