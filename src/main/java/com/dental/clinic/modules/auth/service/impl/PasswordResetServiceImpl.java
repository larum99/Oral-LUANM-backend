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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

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
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String frontendBaseUrl;
    private final String mailFrom;

    public PasswordResetServiceImpl(UserRepository userRepository,
                                    PasswordResetRepository passwordResetRepository,
                                    BCryptPasswordEncoder passwordEncoder,
                                    ObjectProvider<JavaMailSender> mailSenderProvider,
                                    @Value("${app.frontend.base-url:http://localhost:5500}") String frontendBaseUrl,
                                    @Value("${app.mail.from:no-reply@oralluanm.com}") String mailFrom) {
        this.userRepository = userRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderProvider = mailSenderProvider;
        this.frontendBaseUrl = trimTrailingSlash(frontendBaseUrl);
        this.mailFrom = mailFrom;
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
            sendPasswordResetEmail(user, token);
        });
        return new MessageResponse("Si el correo existe, enviaremos instrucciones para restablecer la contrasena.");
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

    private void sendPasswordResetEmail(User user, String token) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("SMTP no esta configurado. No se pudo enviar correo de recuperacion para {}.", user.getEmail());
            return;
        }

        String resetUrl = UriComponentsBuilder
                .fromUriString(frontendBaseUrl)
                .path("/registro/restablecer-contrasena/")
                .queryParam("token", token)
                .build()
                .toUriString();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(user.getEmail());
        message.setSubject("Restablece tu contrasena - ORAL LUANM");
        message.setText("Hola " + safeName(user) + ",\n\n"
                + "Recibimos una solicitud para restablecer tu contrasena. "
                + "Ingresa al siguiente enlace durante los proximos 30 minutos:\n\n"
                + resetUrl + "\n\n"
                + "Si no solicitaste este cambio, puedes ignorar este mensaje.");

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.error("No fue posible enviar correo de recuperacion a {}", user.getEmail(), ex);
        }
    }

    private String safeName(User user) {
        return user.getFirstname() == null || user.getFirstname().isBlank() ? "" : user.getFirstname().trim();
    }

    private String trimTrailingSlash(String value) {
        return value == null ? "" : value.replaceAll("/+$", "");
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