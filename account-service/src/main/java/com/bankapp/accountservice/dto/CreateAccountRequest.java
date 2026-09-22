package com.bankapp.accountservice.dto;

import jakarta.validation.constraints.Pattern;

public record CreateAccountRequest(
        @Pattern(regexp = "SAVINGS|CHECKING", message = "accountType must be SAVINGS or CHECKING")
        String accountType
) {
    // No "username" field, no "balance" field - same rule as customer-service's
    // CreateProfileRequest. The owner always comes from the caller's own JWT,
    // and every account starts at zero; there is no "open with a starting
    // balance" here - fund it with a deposit after opening, like a real bank.
}
