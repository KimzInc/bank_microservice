package com.bankapp.authservice.config;

import com.bankapp.authservice.entity.Role;
import com.bankapp.authservice.entity.User;
import com.bankapp.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

/**
 * Solves the bootstrapping problem: assigning a teller/manager/admin role
 * requires an existing ROLE_ADMIN user to call the assignRole endpoint, but
 * self-registration only ever produces ROLE_CUSTOMER (see RegisterRequest).
 * Without SOME admin already existing, no one could ever create the first one.
 *
 * Idempotent - checks for an existing username before creating anything, so
 * this is safe to leave running across restarts. Credentials come from
 * config-repo/auth-service.yml (app.admin.*), not hardcoded here.
 *
 * This pattern is fine for a learning project; a real system would use a
 * proper one-time bootstrap script or an out-of-band admin creation process,
 * not a seeded password baked into config.
 */
@Component
public class AdminSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;
    private final String adminEmail;

    public AdminSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${a" +
                    "pp.admin.username}") String adminUsername,
            @Value("${app.admin.password}") String adminPassword,
            @Value("${app.admin.email}") String adminEmail
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.adminEmail = adminEmail;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByUsername(adminUsername)) {
            return;
        }

        User admin = User.builder()
                .username(adminUsername)
                .email(adminEmail)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .roles(EnumSet.of(Role.ROLE_ADMIN))
                .enabled(true)
                .build();

        userRepository.save(admin);
    }
}
