package com.example.digital_wallet.controller;

import com.example.digital_wallet.dto.TransactionRequest;
import com.example.digital_wallet.dto.TransactionResponse;
import com.example.digital_wallet.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/deposit/{walletId}")
    public ResponseEntity<TransactionResponse> deposit(@PathVariable Long walletId,
                                                       @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createDeposit(walletId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/withdraw/{walletId}")
    public ResponseEntity<TransactionResponse> withdraw(@PathVariable Long walletId,
                                                        @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createWithdrawal(walletId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/transfer/{fromWalletId}/{toWalletId}")
    public ResponseEntity<TransactionResponse> transfer(@PathVariable Long fromWalletId,
                                                        @PathVariable Long toWalletId,
                                                        @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createTransfer(fromWalletId, toWalletId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/history/{walletId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(@PathVariable Long walletId) {
        List<TransactionResponse> transactions = transactionService.getTransactionHistory(walletId);
        return ResponseEntity.ok(transactions);
    }
}
