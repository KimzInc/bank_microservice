package com.bankapp.customerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

/**
 * Owns customer PROFILE data (name, phone, address, DOB) - the people who hold
 * accounts and apply for loans. Distinct from auth-service, which owns identity
 * and credentials. Linked to an auth-service User by username, not a foreign key -
 * there is no cross-database FK in microservices.
 *
 * UserDetailsServiceAutoConfiguration is excluded because this service never
 * authenticates via AuthenticationManager/UserDetailsService - it only validates
 * JWTs someone else already issued (see security/JwtAuthenticationFilter). Without
 * this exclusion, Spring Boot creates and logs a random-password fallback login
 * on every startup that nothing in this service actually uses.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}