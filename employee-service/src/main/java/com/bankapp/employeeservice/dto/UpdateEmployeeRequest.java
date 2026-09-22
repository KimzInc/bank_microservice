package com.bankapp.employeeservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEmployeeRequest(

        @NotBlank(message = "jobTitle is required")
        @Size(max = 100)
        String jobTitle,

        @Size(max = 100)
        String department
) {
    // Deliberately narrow: job title and department only. Changing someone's
    // role goes through auth-service's role endpoint, not here - see the design
    // note in EmployeeController.
}
