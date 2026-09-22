package com.bankapp.employeeservice.controller;

import com.bankapp.employeeservice.dto.EmployeeProfileResponse;
import com.bankapp.employeeservice.dto.OnboardEmployeeRequest;
import com.bankapp.employeeservice.dto.UpdateEmployeeRequest;
import com.bankapp.employeeservice.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Onboards an existing auth-service user (they must have already
     * self-registered) as staff, assigning them a role via auth-service and
     * creating their HR profile here. Admin-only - this is how the very first
     * teller/manager gets created, using the seeded admin account
     * (see auth-service's AdminSeeder) to bootstrap everyone after it.
     *
     * The caller's own bearer token is forwarded to auth-service as-is (see
     * AuthServiceClient) rather than reconstructing one, which is why we read
     * the raw Authorization header here instead of only using Authentication.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EmployeeProfileResponse> onboard(
            @Valid @RequestBody OnboardEmployeeRequest request,
            HttpServletRequest httpRequest
    ) {
        String bearerToken = httpRequest.getHeader("Authorization");
        EmployeeProfileResponse response = employeeService.onboard(request, bearerToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<EmployeeProfileResponse> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(employeeService.getProfileByUsername(authentication.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<EmployeeProfileResponse> getProfileById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(employeeService.getProfileById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<EmployeeProfileResponse> updateProfile(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateEmployeeRequest request
    ) {
        return ResponseEntity.ok(employeeService.updateProfile(id, request));
    }
}
