package com.bankapp.employeeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

/**
 * Owns employee HR profile data (job title, department, hire date). Does NOT
 * own roles (teller/manager/admin) - those live in auth-service's User entity,
 * because the JWT that carries them is issued and validated there. Onboarding
 * an employee here triggers a real HTTP call to auth-service, resolved via
 * Eureka - see client/AuthServiceClient.
 *
 * UserDetailsServiceAutoConfiguration is excluded for the same reason as
 * customer-service: this service authenticates purely via JWT validation, never
 * via AuthenticationManager/UserDetailsService, so the random-password fallback
 * login Spring Boot would otherwise create and log on every startup is unused.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class EmployeeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeServiceApplication.class, args);
    }
}