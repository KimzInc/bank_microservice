package com.bankapp.accountservice.dto;

import com.bankapp.accountservice.entity.Account;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        Long id,
        String accountNumber,
        String accountType,
        BigDecimal balance,
        String username,
        boolean active,
        Instant createdAt
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType().name(),
                account.getBalance(),
                account.getUsername(),
                account.isActive(),
                account.getCreatedAt()
        );
    }
}
