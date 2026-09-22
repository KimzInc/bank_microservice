package com.bankapp.employeeservice.dto;

import com.bankapp.employeeservice.entity.Employee;

import java.time.LocalDate;
import java.util.Set;

public record EmployeeProfileResponse(
        Long id,
        String username,
        String employeeCode,
        String firstName,
        String lastName,
        String jobTitle,
        String department,
        LocalDate hireDate,
        Set<String> roles
) {
    public static EmployeeProfileResponse from(Employee employee, Set<String> roles) {
        return new EmployeeProfileResponse(
                employee.getId(),
                employee.getUsername(),
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getJobTitle(),
                employee.getDepartment(),
                employee.getHireDate(),
                roles
        );
    }

    /** Used when we don't have (or don't need) a fresh roles lookup from auth-service. */
    public static EmployeeProfileResponse from(Employee employee) {
        return from(employee, Set.of());
    }
}
