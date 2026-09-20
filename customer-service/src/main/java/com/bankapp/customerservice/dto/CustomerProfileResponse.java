package com.bankapp.customerservice.dto;

import com.bankapp.customerservice.entity.Customer;

import java.time.LocalDate;

public record CustomerProfileResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
        String phoneNumber,
        String address,
        LocalDate dateOfBirth
) {
    public static CustomerProfileResponse from(Customer customer) {
        return new CustomerProfileResponse(
                customer.getId(),
                customer.getUsername(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhoneNumber(),
                customer.getAddress(),
                customer.getDateOfBirth()
        );
    }
}
