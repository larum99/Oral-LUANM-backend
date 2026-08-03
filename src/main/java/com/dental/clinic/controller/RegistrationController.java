package com.dental.clinic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/register")
public class RegistrationController {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    public RegistrationController(JdbcTemplate jdbcTemplate, BCryptPasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> registerPatient(@RequestBody Map<String, Object> body) {
        String email = text(body, "correo", text(body, "email")).toLowerCase(Locale.ROOT);
        String password = text(body, "password");
        String documentNumber = text(body, "numeroDeDocumento", text(body, "documentNumber"));
        if (email.isBlank() || password.length() < 8 || documentNumber.isBlank()) {
            throw new IllegalArgumentException("Correo, documento y contrasena minima de 8 caracteres son obligatorios.");
        }
        Integer existing = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE LOWER(email) = ?", Integer.class, email);
        if (existing != null && existing > 0) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
        }
        Long patientRoleId = jdbcTemplate.queryForObject("SELECT id_role FROM roles WHERE name = 'PACIENTE'", Long.class);
        jdbcTemplate.update("""
                INSERT INTO users (id_role, first_name, last_name, email, phone, password_hash, status)
                VALUES (?, ?, ?, ?, ?, ?, 'ACTIVO')
                """, patientRoleId, text(body, "nombre", text(body, "firstName")), nullable(text(body, "apellido", text(body, "lastName"))), email, nullable(text(body, "telefono", text(body, "phone"))), passwordEncoder.encode(password));
        Long userId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        jdbcTemplate.update("""
                INSERT INTO patients (id_user, document_type, document_number, birth_date, accepts_data, accepts_promotions)
                VALUES (?, ?, ?, ?, ?, ?)
                """, userId, text(body, "tipoDocumento", text(body, "documentType", "CC")), documentNumber, optionalDate(text(body, "fechaDeNacimiento", text(body, "birthDate"))), true, false);
        return Map.of("id", userId, "email", email, "role", "client", "message", "Usuario registrado correctamente.");
    }

    private LocalDate optionalDate(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }

    private String nullable(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private String text(Map<String, Object> body, String key) {
        return text(body, key, "");
    }

    private String text(Map<String, Object> body, String key, String fallback) {
        Object value = body.get(key);
        String clean = value == null ? "" : String.valueOf(value).trim();
        return clean.isBlank() ? fallback : clean;
    }
}