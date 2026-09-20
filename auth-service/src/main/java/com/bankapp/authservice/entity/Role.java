package com.bankapp.authservice.entity;

/**
 * Roles drive @PreAuthorize checks in this and every other service that trusts
 * this service's JWTs. CUSTOMER is the default for public self-registration;
 * the employee roles are meant to be assigned by an admin later (see the
 * "role assignment" note on the register endpoint), not chosen freely by whoever
 * is registering.
 */
public enum Role {
    ROLE_CUSTOMER,
    ROLE_TELLER,
    ROLE_MANAGER,
    ROLE_ADMIN
}
