package com.bankapp.accountservice.dto;

import com.bankapp.accountservice.entity.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        Long id,
        String type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String performedBy,
        Instant createdAt
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType().name(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getPerformedBy(),
                transaction.getCreatedAt()
        );
    }
}
