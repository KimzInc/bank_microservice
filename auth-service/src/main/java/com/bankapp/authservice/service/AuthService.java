package com.bankapp.authservice.service;

import com.bankapp.authservice.dto.AuthResponse;
import com.bankapp.authservice.dto.LoginRequest;
import com.bankapp.authservice.dto.RegisterRequest;
import com.bankapp.authservice.dto.UserRolesResponse;
import com.bankapp.authservice.entity.Role;
import com.bankapp.authservice.entity.User;
import com.bankapp.authservice.exception.EmailAlreadyExistsException;
import com.bankapp.authservice.exception.InvalidCredentialsException;
import com.bankapp.authservice.exception.InvalidRoleException;
import com.bankapp.authservice.exception.UserNotFoundException;
import com.bankapp.authservice.exception.UsernameAlreadyExistsException;
import com.bankapp.authservice.repository.UserRepository;
import com.bankapp.authservice.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .roles(EnumSet.of(Role.ROLE_CUSTOMER))
                .enabled(true)
                .build();

        userRepository.save(user);

        return issueTokenFor(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isEnabled() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return issueTokenFor(user);
    }

    /**
     * Adds a role to an existing user - called by employee-service when an admin
     * onboards someone as a teller/manager/admin. Deliberately ADDS to the
     * existing role set rather than replacing it: someone can be both a
     * ROLE_CUSTOMER (their personal account) and a ROLE_TELLER (their job),
     * which is realistic for a small bank. This endpoint itself is protected
     * by @PreAuthorize("hasAuthority('ROLE_ADMIN')") at the controller level,
     * not here.
     */
    @Transactional
    public UserRolesResponse assignRole(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        Role role;
        try {
            role = Role.valueOf(roleName);
        } catch (IllegalArgumentException ex) {
            throw new InvalidRoleException(roleName);
        }

        user.getRoles().add(role);
        userRepository.save(user);

        Set<String> roleNames = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        return new UserRolesResponse(user.getUsername(), roleNames);
    }

    private AuthResponse issueTokenFor(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        String token = jwtService.generateToken(user.getUsername(), roleNames);
        return AuthResponse.of(token, jwtService.getExpirationMs(), user.getUsername(), roleNames);
    }
}
