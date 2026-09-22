package com.bankapp.employeeservice.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String identifier) {
        super("No employee profile found for '" + identifier + "'");
    }
}
