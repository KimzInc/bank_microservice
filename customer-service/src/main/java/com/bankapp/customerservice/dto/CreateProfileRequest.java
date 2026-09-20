package com.bankapp.customerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateProfileRequest(

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
    // No "username" field on purpose: the profile is always created for whoever
    // the caller's validated JWT says they are (see CustomerController), never
    // for a username supplied in the request body.
}
