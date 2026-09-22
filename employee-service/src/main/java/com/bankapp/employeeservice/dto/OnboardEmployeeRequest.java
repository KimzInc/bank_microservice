package com.bankapp.employeeservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record OnboardEmployeeRequest(

        @NotBlank(message = "username is required")
        @Size(max = 50)
        String username,

        @NotBlank(message = "employeeCode is required")
        @Size(max = 20)
        String employeeCode,

        @NotBlank(message = "firstName is required")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "lastName is required")
        @Size(max = 100)
        String lastName,

        @NotBlank(message = "jobTitle is required")
        @Size(max = 100)
        String jobTitle,

        @Size(max = 100)
        String department,

        @Past(message = "hireDate must be in the past")
        LocalDate hireDate,

        @NotBlank(message = "role is required")
        @Pattern(regexp = "ROLE_TELLER|ROLE_MANAGER|ROLE_ADMIN",
                message = "role must be one of ROLE_TELLER, ROLE_MANAGER, ROLE_ADMIN")
        String role
) {
    // "username" must belong to someone who has already self-registered via
    // auth-service - this endpoint promotes an existing user into a staff role,
    // it does not create login credentials. role is restricted to the three
    // employee roles on purpose: onboarding can't grant ROLE_CUSTOMER (everyone
    // already has that from registration, or doesn't need it as staff-only).
}
