package com.bankapp.employeeservice.exception;

public class RoleAssignmentException extends RuntimeException {
    public RoleAssignmentException(String message) {
        super(message);
    }

    public RoleAssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
