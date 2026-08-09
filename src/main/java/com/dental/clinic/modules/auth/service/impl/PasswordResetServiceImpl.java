package com.dental.clinic.modules.auth.service.impl;

import com.dental.clinic.modules.auth.dto.request.PasswordResetConfirmRequest;
import com.dental.clinic.modules.auth.dto.request.PasswordResetRequest;
import com.dental.clinic.shared.response.MessageResponse;
import com.dental.clinic.modules.auth.entity.PasswordReset;
import com.dental.clinic.modules.auth.entity.User;
import com.dental.clinic.shared.exception.BusinessException;
import com.dental.clinic.modules.auth.repository.PasswordResetRepository;
import com.dental.clinic.modules.auth.repository.UserRepository;
import com.dental.clinic.modules.auth.service.PasswordResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordResetServiceImpl(UserRepository userRepository,
                                    PasswordResetRepository passwordResetRepository,
                                    BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public MessageResponse requestReset(PasswordResetRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            String token = UUID.randomUUID() + "-" + UUID.randomUUID();
            PasswordReset reset = new PasswordReset();
            reset.setUser(user);
            reset.setTokenHash(sha256(token));
            reset.setExpiresAt(LocalDateTime.now().plusMinutes(30));
            passwordResetRepository.save(reset);
            log.info("Password reset token generated for {}. Configure SMTP provider to deliver it securely.", email);
        });
        return new MessageResponse("Si el correo existe, se genero una solicitud de recuperacion.");
    }

    @Override
    @Transactional
    public void confirmReset(PasswordResetConfirmRequest request) {
        PasswordReset reset = passwordResetRepository.findByTokenHashAndUsedAtIsNullAndExpiresAtAfter(sha256(request.token()), LocalDateTime.now())
                .orElseThrow(() -> new BusinessException("Token invalido o expirado."));
        User user = reset.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        reset.setUsedAt(LocalDateTime.now());
        userRepository.save(user);
        passwordResetRepository.save(reset);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("No fue posible procesar el token de recuperacion.", ex);
        }
    }
}
