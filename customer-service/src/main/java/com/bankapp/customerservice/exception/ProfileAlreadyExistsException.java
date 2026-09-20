package com.bankapp.customerservice.exception;

public class ProfileAlreadyExistsException extends RuntimeException {
    public ProfileAlreadyExistsException(String username) {
        super("A profile already exists for user '" + username + "'");
    }
}
