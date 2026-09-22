package com.bankapp.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

/**
 * Owns bank accounts, balances, deposits and withdrawals. Linked to an
 * auth-service User by username, same pattern as customer-service and
 * employee-service - no cross-database foreign key.
 *
 * UserDetailsServiceAutoConfiguration excluded for the same reason as those
 * two: this service authenticates purely via JWT validation, never via
 * AuthenticationManager/UserDetailsService.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}
