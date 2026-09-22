package com.bankapp.accountservice.exception;

/**
 * Thrown when the caller is neither the account's owner nor staff
 * (ROLE_TELLER/MANAGER/ADMIN). Distinct from Spring Security's own
 * AccessDeniedException because this decision depends on WHICH account is
 * being accessed, not a static per-endpoint role rule - see SecurityConfig.
 */
public class AccountAccessDeniedException extends RuntimeException {
    public AccountAccessDeniedException(String message) {
        super(message);
    }
}
