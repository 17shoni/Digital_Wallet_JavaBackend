package com.example.digital_wallet.controller;

import com.example.digital_wallet.model.Wallet;
import com.example.digital_wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getWalletById(@PathVariable Long id) {
        Wallet wallet = walletService.findWalletById(id);
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Wallet> getWalletByUserId(@PathVariable Long userId) {
        Wallet wallet = walletService.findWalletByUserId(userId);
        return ResponseEntity.ok(wallet);
    }
}
