package com.bankapp.accountservice.service;

import com.bankapp.accountservice.dto.AccountResponse;
import com.bankapp.accountservice.dto.CreateAccountRequest;
import com.bankapp.accountservice.dto.TransactionResponse;
import com.bankapp.accountservice.entity.Account;
import com.bankapp.accountservice.entity.AccountType;
import com.bankapp.accountservice.entity.Transaction;
import com.bankapp.accountservice.entity.TransactionType;
import com.bankapp.accountservice.exception.AccountAccessDeniedException;
import com.bankapp.accountservice.exception.AccountNotFoundException;
import com.bankapp.accountservice.exception.InsufficientFundsException;
import com.bankapp.accountservice.repository.AccountRepository;
import com.bankapp.accountservice.repository.TransactionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Set;

@Service
public class AccountService {

    private static final Set<String> STAFF_ROLES = Set.of("ROLE_TELLER", "ROLE_MANAGER", "ROLE_ADMIN");
    private static final SecureRandom RANDOM = new SecureRandom();

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public AccountResponse openAccount(String username, CreateAccountRequest request) {
        Account account = Account.builder()
                .accountNumber(generateUniqueAccountNumber())
                .username(username)
                .accountType(AccountType.valueOf(request.accountType()))
                .balance(BigDecimal.ZERO)
                .active(true)
                .build();

        accountRepository.save(account);
        return AccountResponse.from(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getMyAccounts(String username) {
        return accountRepository.findByUsername(username).stream()
                .map(AccountResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id, Authentication caller) {
        Account account = findOrThrow(id);
        requireOwnerOrStaff(account, caller);
        return AccountResponse.from(account);
    }

    @Transactional
    public TransactionResponse deposit(Long id, BigDecimal amount, Authentication caller) {
        // findByIdForUpdate, not findById: see AccountRepository for why this
        // matters the moment more than one request can hit the same account.
        Account account = accountRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        requireOwnerOrStaff(account, caller);

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .balanceAfter(newBalance)
                .performedBy(caller.getName())
                .build();
        transactionRepository.save(transaction);

        return TransactionResponse.from(transaction);
    }

    @Transactional
    public TransactionResponse withdraw(Long id, BigDecimal amount, Authentication caller) {
        Account account = accountRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        requireOwnerOrStaff(account, caller);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(account.getBalance(), amount);
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .account(account)
                .type(TransactionType.WITHDRAWAL)
                .amount(amount)
                .balanceAfter(newBalance)
                .performedBy(caller.getName())
                .build();
        transactionRepository.save(transaction);

        return TransactionResponse.from(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(Long id, Authentication caller) {
        Account account = findOrThrow(id);
        requireOwnerOrStaff(account, caller);

        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(account.getId()).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    private Account findOrThrow(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    /**
     * Not expressible as a static @PreAuthorize rule (see SecurityConfig) because
     * it depends on the specific account being accessed: the caller must either
     * OWN it, or hold a staff role and be acting on someone else's.
     */
    private void requireOwnerOrStaff(Account account, Authentication caller) {
        boolean isOwner = account.getUsername().equals(caller.getName());
        boolean isStaff = caller.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(STAFF_ROLES::contains);

        if (!isOwner && !isStaff) {
            throw new AccountAccessDeniedException(
                    "You do not have permission to access account " + account.getId());
        }
    }

    private String generateUniqueAccountNumber() {
        for (int attempt = 0; attempt < 5; attempt++) {
            String candidate = "ACC" + String.format("%09d", RANDOM.nextInt(1_000_000_000));
            if (!accountRepository.existsByAccountNumber(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not generate a unique account number after 5 attempts");
    }
}
