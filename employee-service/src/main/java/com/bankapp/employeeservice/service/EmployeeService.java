package com.bankapp.employeeservice.service;

import com.bankapp.employeeservice.client.AuthServiceClient;
import com.bankapp.employeeservice.dto.EmployeeProfileResponse;
import com.bankapp.employeeservice.dto.OnboardEmployeeRequest;
import com.bankapp.employeeservice.dto.UpdateEmployeeRequest;
import com.bankapp.employeeservice.entity.Employee;
import com.bankapp.employeeservice.exception.EmployeeAlreadyExistsException;
import com.bankapp.employeeservice.exception.EmployeeNotFoundException;
import com.bankapp.employeeservice.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuthServiceClient authServiceClient;

    public EmployeeService(EmployeeRepository employeeRepository, AuthServiceClient authServiceClient) {
        this.employeeRepository = employeeRepository;
        this.authServiceClient = authServiceClient;
    }

    /**
     * Onboards an existing auth-service user as a staff member: assigns the role
     * in auth-service FIRST, then creates the local HR profile. That order
     * matters - see the note below.
     *
     * KNOWN LIMITATION (worth understanding, not fixing yet): this is two
     * separate writes to two separate databases with no distributed
     * transaction between them. If the auth-service call succeeds but this
     * service's own save() then fails (e.g. a duplicate employeeCode), the
     * user ends up with a staff role in auth-service but no HR profile here -
     * an inconsistent state this method cannot roll back on its own. Calling
     * auth-service first, rather than saving locally first, at least avoids
     * the worse case (an HR profile existing for someone auth-service doesn't
     * think has the role). This exact problem - keeping two databases
     * consistent without a shared transaction - is what the Saga pattern
     * (mentioned in the docs' Kafka section) exists to solve properly.
     */
    @Transactional
    public EmployeeProfileResponse onboard(OnboardEmployeeRequest request, String bearerToken) {
        if (employeeRepository.existsByUsername(request.username())) {
            throw new EmployeeAlreadyExistsException(request.username());
        }
        if (employeeRepository.existsByEmployeeCode(request.employeeCode())) {
            throw new EmployeeAlreadyExistsException(request.employeeCode());
        }

        Set<String> updatedRoles = authServiceClient.assignRole(request.username(), request.role(), bearerToken);

        Employee employee = Employee.builder()
                .username(request.username())
                .employeeCode(request.employeeCode())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .jobTitle(request.jobTitle())
                .department(request.department())
                .hireDate(request.hireDate())
                .build();

        employeeRepository.save(employee);

        return EmployeeProfileResponse.from(employee, updatedRoles);
    }

    @Transactional(readOnly = true)
    public EmployeeProfileResponse getProfileByUsername(String username) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new EmployeeNotFoundException(username));
        return EmployeeProfileResponse.from(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeProfileResponse getProfileById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(String.valueOf(id)));
        return EmployeeProfileResponse.from(employee);
    }

    @Transactional
    public EmployeeProfileResponse updateProfile(Long id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(String.valueOf(id)));

        employee.setJobTitle(request.jobTitle());
        employee.setDepartment(request.department());

        employeeRepository.save(employee);
        return EmployeeProfileResponse.from(employee);
    }
}
