package com.dental.clinic.service.impl;

import com.dental.clinic.dto.request.LoginRequest;
import com.dental.clinic.dto.response.AuthUserResponse;
import com.dental.clinic.entity.Role;
import com.dental.clinic.entity.User;
import com.dental.clinic.exception.AuthenticationException;
import com.dental.clinic.repository.UserRepository;
import com.dental.clinic.security.JwtService;
import com.dental.clinic.service.AuthService;
import com.dental.clinic.utils.MessageConstants;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String ACTIVE_STATUS = "ACTIVO";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthUserResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(this::invalidCredentials);

        if (!ACTIVE_STATUS.equals(user.getStatus()) || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw invalidCredentials();
        }

        user.setLastLogin(LocalDateTime.now());

        return toResponse(user);
    }

    private AuthUserResponse toResponse(User user) {
        String role = toFrontendRole(user.getRole());
        return new AuthUserResponse(
                user.getId(),
                user.getEmail(),
                getDisplayName(user),
                role,
                toDashboardPath(role),
                user.getPhone(),
                jwtService.generateToken(user, role),
                "Bearer",
                jwtService.getExpirationMinutes() * 60
        );
    }

    private String getDisplayName(User user) {
        String firstName = user.getFirstname() == null ? "" : user.getFirstname().trim();
        String lastName = user.getLastName() == null ? "" : user.getLastName().trim();
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isBlank() ? user.getEmail() : fullName;
    }

    private String toFrontendRole(Role role) {
        String roleName = role == null ? "PACIENTE" : role.getName();
        return switch (roleName) {
            case "ADMIN" -> "admin";
            case "SECRETARIO" -> "secretario";
            case "ESPECIALISTA" -> "specialist";
            default -> "client";
        };
    }

    private String toDashboardPath(String role) {
        return switch (role) {
            case "admin" -> "registro/admin/";
            case "secretario" -> "registro/secretario/";
            default -> "registro/cliente/";
        };
    }

    private AuthenticationException invalidCredentials() {
        return new AuthenticationException(MessageConstants.AUTH_INVALID_CREDENTIALS);
    }
}
