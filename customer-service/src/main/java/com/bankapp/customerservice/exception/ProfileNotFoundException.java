package com.bankapp.customerservice.exception;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String identifier) {
        super("No customer profile found for '" + identifier + "'");
    }
}
