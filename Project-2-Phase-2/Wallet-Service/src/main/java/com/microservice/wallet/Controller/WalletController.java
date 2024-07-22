package com.microservice.wallet.Controller;

import com.microservice.wallet.Models.WalletDB;
import com.microservice.wallet.Models.PayloadDB;
import com.microservice.wallet.Repository.WalletDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@Transactional(isolation= Isolation.SERIALIZABLE)
public class WalletController {
    @Autowired
    private WalletDBRepository walletRepository;
    @Autowired
    private RestTemplate restTemplate;

    private Lock lock =new ReentrantLock();

    @GetMapping("/wallets/{wallet_id}")
    public ResponseEntity<WalletDB> findWallet(@PathVariable("wallet_id") Integer id){
        lock.lock();
        try{
            WalletDB wallet= walletRepository._findByUser_id(id);
            if(wallet!=null) { // Check if the user has a wallet
                // Returns the wallet details for the user with ID user_id with HTTP status code 200 (OK)
                return new ResponseEntity<>(wallet, HttpStatus.OK);
            }
            else
                // If the user doesn't have a wallet, HTTP status code 404 (Not Found)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        finally {
            lock.unlock();
        }

    }

    @PutMapping(value = "/wallets/{wallet_id}")
    public ResponseEntity<WalletDB> putWallet(@PathVariable("wallet_id") Integer id, @RequestBody PayloadDB payload){
        lock.lock();
        try{
            if(payload.getAction().equals("debit") || payload.getAction().equals("credit")) { // Check that we are performing a valid action
                // Fetch the wallet of the user user_id
                WalletDB wallet= walletRepository._findByUser_id(id);
                if(wallet == null){
                    // If the user doesn't have a wallet
                    try{
                        // First check that the user is registered in UserDB
                        restTemplate.getForEntity("http://host.docker.internal:8080/users/"+id, String.class);
                        // If the user exists, the above line will not raise an exception. Now create a new wallet for the user with 0 balance.
                        wallet = walletRepository.save(new WalletDB(id, 0));
                    }
                    catch(Exception e){
                        // If the user doesn't exists, return HTTP status code 404 (NOT FOUND)
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                }
                if(payload.getAction().equals("debit")){
                    // If the action is debit, first check that the user has sufficient balance. If not, do not update the balance, and return n HTTP status code 400 (Bad Request).
                    if(wallet.getBalance() - payload.getAmount() < 0)
                        return new ResponseEntity<>(wallet, HttpStatus.BAD_REQUEST);
                    // Else, deduct the required amount and update the wallet
                    wallet.setBalance(wallet.getBalance() - payload.getAmount());
                    walletRepository.save(wallet);
                }
                else{
                    // If the action is credit, add the required amount and update the wallet
                    wallet.setBalance(wallet.getBalance() + payload.getAmount());
                    walletRepository.save(wallet);
                }
                // Return wallet details with HTTP status code 200 (OK)
                return new ResponseEntity<>(wallet, HttpStatus.OK);
            }
            else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        finally {
            lock.unlock();
        }

    }

    @Transactional
    @DeleteMapping(value = "/wallets/{user_id}")
    public ResponseEntity<WalletDB> deleteWallet(@PathVariable("user_id") Integer id){
        lock.lock();
        try{
            if(walletRepository._findByUser_id(id)!=null){ // Check that the user has a wallet
                // Delete the wallet of the user with ID user_id
                walletRepository._deleteByUser_id(id);
                // Return HTTP status code 200 (OK)
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else
                // If the user does not have a wallet, return an HTTP status code 404 (Not Found)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        finally {
            lock.unlock();
        }

    }

    @Transactional
    @DeleteMapping(value = "/wallets")
    public ResponseEntity<WalletDB> deleteAllWallets(){
        lock.lock();
        try{
            // Deletes the wallets of all users, and returns HTTP status code 200 (OK)
            walletRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        }
        finally {
            lock.unlock();
        }

    }
}

