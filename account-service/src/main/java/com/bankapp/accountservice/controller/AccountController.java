package com.bankapp.accountservice.controller;

import com.bankapp.accountservice.dto.AccountResponse;
import com.bankapp.accountservice.dto.CreateAccountRequest;
import com.bankapp.accountservice.dto.TransactionRequest;
import com.bankapp.accountservice.dto.TransactionResponse;
import com.bankapp.accountservice.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /** Opens a new account owned by the caller - username always comes from the JWT. */
    @PostMapping
    public ResponseEntity<AccountResponse> openAccount(
            Authentication authentication,
            @Valid @RequestBody CreateAccountRequest request
    ) {
        AccountResponse response = accountService.openAccount(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<AccountResponse>> getMyAccounts(Authentication authentication) {
        return ResponseEntity.ok(accountService.getMyAccounts(authentication.getName()));
    }

    /** Owner, or staff (teller/manager/admin) acting on someone else's account. */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(
            @PathVariable("id") Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(accountService.getAccountById(id, authentication));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @PathVariable("id") Long id,
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(accountService.deposit(id, request.amount(), authentication));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @PathVariable("id") Long id,
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(accountService.withdraw(id, request.amount(), authentication));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @PathVariable("id") Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(accountService.getTransactions(id, authentication));
    }
}
