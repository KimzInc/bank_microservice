package com.bankapp.authservice.dto;

import java.util.Set;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresInMs,
        String username,
        Set<String> roles
) {
    public static AuthResponse of(String token, long expiresInMs, String username, Set<String> roles) {
        return new AuthResponse(token, "Bearer", expiresInMs, username, roles);
    }
}
