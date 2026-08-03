package com.dental.clinic.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/password-reset")
public class PasswordResetController {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetController.class);

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordResetController(JdbcTemplate jdbcTemplate, BCryptPasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/request")
    public Map<String, Object> requestReset(@RequestBody Map<String, Object> body) {
        String email = text(body, "email").toLowerCase();
        Long userId = jdbcTemplate.query("SELECT id_user FROM users WHERE LOWER(email) = ?", rs -> rs.next() ? rs.getLong(1) : null, email);
        if (userId != null) {
            String token = UUID.randomUUID() + "-" + UUID.randomUUID();
            jdbcTemplate.update("""
                    INSERT INTO password_resets (id_user, token_hash, expires_at)
                    VALUES (?, ?, ?)
                    """, userId, sha256(token), LocalDateTime.now().plusMinutes(30));
            log.info("Password reset token generated for {}. Configure SMTP to deliver it securely.", email);
        }
        return Map.of("message", "Si el correo existe, se genero una solicitud de recuperacion.");
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmReset(@RequestBody Map<String, Object> body) {
        String tokenHash = sha256(text(body, "token"));
        String password = text(body, "password");
        if (password.length() < 8) {
            throw new IllegalArgumentException("La contrasena debe tener minimo 8 caracteres.");
        }
        Long resetId = jdbcTemplate.query("""
                SELECT id_password_reset
                FROM password_resets
                WHERE token_hash = ? AND used_at IS NULL AND expires_at > CURRENT_TIMESTAMP
                """, rs -> rs.next() ? rs.getLong(1) : null, tokenHash);
        if (resetId == null) {
            throw new IllegalArgumentException("Token invalido o expirado.");
        }
        Long userId = jdbcTemplate.queryForObject("SELECT id_user FROM password_resets WHERE id_password_reset = ?", Long.class, resetId);
        jdbcTemplate.update("UPDATE users SET password_hash = ? WHERE id_user = ?", passwordEncoder.encode(password), userId);
        jdbcTemplate.update("UPDATE password_resets SET used_at = CURRENT_TIMESTAMP WHERE id_password_reset = ?", resetId);
        return ResponseEntity.noContent().build();
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("No fue posible procesar el token de recuperacion.", ex);
        }
    }

    private String text(Map<String, Object> body, String key) {
        Object value = body.get(key);
        return value == null ? "" : String.valueOf(value).trim();
    }
}