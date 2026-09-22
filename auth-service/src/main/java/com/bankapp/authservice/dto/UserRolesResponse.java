package com.bankapp.authservice.dto;

import java.util.Set;

public record UserRolesResponse(String username, Set<String> roles) {
}
