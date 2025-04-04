package com.example.digital_wallet.service;

import com.example.digital_wallet.dto.TransactionRequest;
import com.example.digital_wallet.dto.TransactionResponse;
import com.example.digital_wallet.model.Transaction;
import com.example.digital_wallet.model.TransactionStatus;
import com.example.digital_wallet.model.TransactionType;
import com.example.digital_wallet.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletService walletService;

    @Transactional
    public TransactionResponse createDeposit(Long walletId, TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setWallet(walletService.findWalletById(walletId));
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(TransactionStatus.COMPLETED);

        // Update wallet balance
        walletService.updateBalance(walletId, request.getAmount(), false);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTransaction);
    }

    @Transactional
    public TransactionResponse createWithdrawal(Long walletId, TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setWallet(walletService.findWalletById(walletId));
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(TransactionStatus.COMPLETED);

        // Update wallet balance
        walletService.updateBalance(walletId, request.getAmount(), true);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTransaction);
    }

    @Transactional
    public TransactionResponse createTransfer(Long fromWalletId, Long toWalletId, TransactionRequest request) {
        walletService.findWalletById(toWalletId); // Validate recipient wallet exists

        Transaction sentTransaction = new Transaction();
        sentTransaction.setWallet(walletService.findWalletById(fromWalletId));
        sentTransaction.setType(TransactionType.TRANSFER_SENT);
        sentTransaction.setAmount(request.getAmount());
        sentTransaction.setDescription(request.getDescription());
        sentTransaction.setCounterpartyWalletId(toWalletId);
        sentTransaction.setStatus(TransactionStatus.COMPLETED);

        Transaction receivedTransaction = new Transaction();
        receivedTransaction.setWallet(walletService.findWalletById(toWalletId));
        receivedTransaction.setType(TransactionType.TRANSFER_RECEIVED);
        receivedTransaction.setAmount(request.getAmount());
        receivedTransaction.setDescription(request.getDescription());
        receivedTransaction.setCounterpartyWalletId(fromWalletId);
        receivedTransaction.setStatus(TransactionStatus.COMPLETED);

        // Update wallet balances
        walletService.updateBalance(fromWalletId, request.getAmount(), true); // Deduct from sender
        walletService.updateBalance(toWalletId, request.getAmount(), false); // Add to recipient

        transactionRepository.save(sentTransaction);
        transactionRepository.save(receivedTransaction);

        return mapToTransactionResponse(sentTransaction); // Return sender's transaction
    }

    public List<TransactionResponse> getTransactionHistory(Long walletId) {
        List<Transaction> transactions = transactionRepository.findByWalletId(walletId);
        return transactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setWalletId(transaction.getWallet().getId());
        response.setType(transaction.getType());
        response.setAmount(transaction.getAmount());
        response.setDescription(transaction.getDescription());
        response.setTimestamp(transaction.getTimestamp());
        response.setStatus(transaction.getStatus());
        response.setCounterpartyWalletId(transaction.getCounterpartyWalletId());
        return response;
    }
}
