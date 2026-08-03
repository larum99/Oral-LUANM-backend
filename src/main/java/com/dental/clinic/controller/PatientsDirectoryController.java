package com.dental.clinic.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PatientsDirectoryController {

    private final JdbcTemplate jdbcTemplate;

    public PatientsDirectoryController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/patients-directory")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA')")
    public List<Map<String, Object>> patientsDirectory() {
        return jdbcTemplate.queryForList("""
                SELECT p.id_patient patientId, p.id_user id, p.document_type documentType,
                       p.document_number patientDocument, p.birth_date birthDate,
                       TRIM(CONCAT(u.first_name, ' ', COALESCE(u.last_name, ''))) name,
                       u.email, u.phone, u.status, 'Cliente' role
                FROM patients p
                LEFT JOIN users u ON u.id_user = p.id_user
                ORDER BY p.id_patient
                """);
    }
}