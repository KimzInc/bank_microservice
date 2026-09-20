package com.bankapp.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Owns registration, login, and JWT issuing for both customers and employees.
 * Every other service in this system trusts the identity and roles encoded
 * in the JWT this service issues - it does not re-implement authentication itself.
 */
@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
