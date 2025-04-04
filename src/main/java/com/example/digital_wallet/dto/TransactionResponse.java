package com.example.digital_wallet.dto;

import com.example.digital_wallet.model.TransactionStatus;
import com.example.digital_wallet.model.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private Long walletId;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;
    private TransactionStatus status;
    private Long counterpartyWalletId;
}
