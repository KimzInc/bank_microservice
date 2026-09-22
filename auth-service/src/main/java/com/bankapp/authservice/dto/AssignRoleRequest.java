package com.bankapp.authservice.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignRoleRequest(
        @NotBlank(message = "role is required")
        String role
) {
    // Accepts a role name matching the Role enum, e.g. "ROLE_TELLER". Validated
    // and parsed in AuthService.assignRole - an unrecognized value is rejected
    // with a 400, not silently ignored.
}
