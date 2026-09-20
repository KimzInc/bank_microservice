package com.bankapp.customerservice.controller;

import com.bankapp.customerservice.dto.CreateProfileRequest;
import com.bankapp.customerservice.dto.CustomerProfileResponse;
import com.bankapp.customerservice.dto.UpdateProfileRequest;
import com.bankapp.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Creates the caller's OWN profile. "username" is taken from the validated
     * JWT (Authentication.getName()), never from the request body - see
     * CreateProfileRequest for why.
     */
    @PostMapping("/me")
    public ResponseEntity<CustomerProfileResponse> createMyProfile(
            Authentication authentication,
            @Valid @RequestBody CreateProfileRequest request
    ) {
        CustomerProfileResponse response = customerService.createProfile(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerProfileResponse> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(customerService.getProfileByUsername(authentication.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<CustomerProfileResponse> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(customerService.updateProfile(authentication.getName(), request));
    }

    /**
     * Employee-only lookup by internal ID - e.g. a teller pulling up a customer's
     * profile while handling a deposit. Customers cannot use this to view each
     * other's profiles; only the /me endpoints above are available to ROLE_CUSTOMER.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_TELLER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<CustomerProfileResponse> getProfileById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(customerService.getProfileById(id));
    }
}
