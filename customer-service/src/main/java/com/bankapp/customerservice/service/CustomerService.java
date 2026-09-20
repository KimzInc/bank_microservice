package com.bankapp.customerservice.service;

import com.bankapp.customerservice.dto.CreateProfileRequest;
import com.bankapp.customerservice.dto.CustomerProfileResponse;
import com.bankapp.customerservice.dto.UpdateProfileRequest;
import com.bankapp.customerservice.entity.Customer;
import com.bankapp.customerservice.exception.ProfileAlreadyExistsException;
import com.bankapp.customerservice.exception.ProfileNotFoundException;
import com.bankapp.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerProfileResponse createProfile(String username, CreateProfileRequest request) {
        if (customerRepository.existsByUsername(username)) {
            throw new ProfileAlreadyExistsException(username);
        }

        Customer customer = Customer.builder()
                .username(username)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phoneNumber(request.phoneNumber())
                .address(request.address())
                .dateOfBirth(request.dateOfBirth())
                .build();

        customerRepository.save(customer);
        return CustomerProfileResponse.from(customer);
    }

    @Transactional(readOnly = true)
    public CustomerProfileResponse getProfileByUsername(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ProfileNotFoundException(username));
        return CustomerProfileResponse.from(customer);
    }

    @Transactional(readOnly = true)
    public CustomerProfileResponse getProfileById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(String.valueOf(id)));
        return CustomerProfileResponse.from(customer);
    }

    @Transactional
    public CustomerProfileResponse updateProfile(String username, UpdateProfileRequest request) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ProfileNotFoundException(username));

        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setAddress(request.address());
        customer.setDateOfBirth(request.dateOfBirth());

        customerRepository.save(customer);
        return CustomerProfileResponse.from(customer);
    }
}
