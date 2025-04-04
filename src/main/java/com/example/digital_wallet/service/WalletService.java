package com.example.digital_wallet.service;

import com.example.digital_wallet.model.Wallet;
import com.example.digital_wallet.exception.InsufficientBalanceException;
import com.example.digital_wallet.exception.ResourceNotFoundException;
import com.example.digital_wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public Wallet findWalletById(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + id));
    }

    public Wallet findWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user id: " + userId));
    }

    public void updateBalance(Long walletId, BigDecimal amount, boolean isDeduction) {
        Wallet wallet = findWalletById(walletId);
        BigDecimal newBalance = isDeduction
                ? wallet.getBalance().subtract(amount)
                : wallet.getBalance().add(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in wallet: " + walletId);
        }

        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
    }
}