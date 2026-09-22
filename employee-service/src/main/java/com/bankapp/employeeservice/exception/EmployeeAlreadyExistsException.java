package com.bankapp.employeeservice.exception;

public class EmployeeAlreadyExistsException extends RuntimeException {
    public EmployeeAlreadyExistsException(String username) {
        super("An employee profile already exists for user '" + username + "'");
    }
}
