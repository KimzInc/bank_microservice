package com.bankapp.authservice.exception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String role) {
        super("'" + role + "' is not a recognized role");
    }
}
