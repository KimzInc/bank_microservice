package com.bankapp.customerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProfileRequest(

        @NotBlank(message = "firstName is required")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "lastName is required")
        @Size(max = 100)
        String lastName,

        @Size(max = 20)
        String phoneNumber,

        @Size(max = 255)
        String address,

        @Past(message = "dateOfBirth must be in the past")
        LocalDate dateOfBirth
) {
}
