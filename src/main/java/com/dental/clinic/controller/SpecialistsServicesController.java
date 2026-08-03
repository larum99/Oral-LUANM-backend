package com.dental.clinic.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SpecialistsServicesController {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    public SpecialistsServicesController(JdbcTemplate jdbcTemplate, BCryptPasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/specialists")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA')")
    public List<Map<String, Object>> specialists() {
        return jdbcTemplate.queryForList("""
                SELECT s.id_specialist id, s.id_user userId,
                       TRIM(CONCAT(u.first_name, ' ', COALESCE(u.last_name, ''))) name,
                       u.email, u.phone, s.specialty, s.professional_license professionalLicense,
                       CASE WHEN s.active THEN 'Activo' ELSE 'Inactivo' END status
                FROM specialists s
                INNER JOIN users u ON u.id_user = s.id_user
                ORDER BY s.id_specialist
                """);
    }

    @PostMapping("/specialists")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> createSpecialist(@RequestBody Map<String, Object> body) {
        Long userId = optionalNumber(body, "userId");
        if (userId == null) {
            String email = text(body, "email").toLowerCase(Locale.ROOT);
            String password = text(body, "password");
            if (email.isBlank() || password.length() < 8) {
                throw new IllegalArgumentException("Correo y contrasena minima de 8 caracteres son obligatorios para crear especialista.");
            }
            Long roleId = jdbcTemplate.queryForObject("SELECT id_role FROM roles WHERE name = 'ESPECIALISTA'", Long.class);
            String[] names = splitName(text(body, "name"));
            jdbcTemplate.update("""
                    INSERT INTO users (id_role, first_name, last_name, email, phone, password_hash, status)
                    VALUES (?, ?, ?, ?, ?, ?, 'ACTIVO')
                    """, roleId, names[0], nullable(names[1]), email, nullable(text(body, "phone")), passwordEncoder.encode(password));
            userId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        }
        jdbcTemplate.update("""
                INSERT INTO specialists (id_user, specialty, professional_license, active)
                VALUES (?, ?, ?, ?)
                """, userId, text(body, "specialty"), nullable(text(body, "professionalLicense")), active(body));
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return jdbcTemplate.queryForMap("SELECT id_specialist id, id_user userId, specialty, professional_license professionalLicense, active FROM specialists WHERE id_specialist = ?", id);
    }

    @PutMapping("/specialists/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> updateSpecialist(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        jdbcTemplate.update("""
                UPDATE specialists SET specialty = ?, professional_license = ?, active = ? WHERE id_specialist = ?
                """, text(body, "specialty"), nullable(text(body, "professionalLicense")), active(body), id);
        return jdbcTemplate.queryForMap("SELECT id_specialist id, id_user userId, specialty, professional_license professionalLicense, active FROM specialists WHERE id_specialist = ?", id);
    }

    @GetMapping("/services")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA','PACIENTE')")
    public List<Map<String, Object>> services() {
        return jdbcTemplate.queryForList("SELECT id_service id, name, description, duration_minutes durationMinutes, price, active FROM services ORDER BY name");
    }

    @PostMapping("/services")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> createService(@RequestBody Map<String, Object> body) {
        jdbcTemplate.update("""
                INSERT INTO services (name, description, duration_minutes, price, active)
                VALUES (?, ?, ?, ?, ?)
                """, text(body, "name"), nullable(text(body, "description")), numberOrDefault(body, "durationMinutes", 30), decimalText(body, "price"), active(body));
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return jdbcTemplate.queryForMap("SELECT id_service id, name, description, duration_minutes durationMinutes, price, active FROM services WHERE id_service = ?", id);
    }

    @PutMapping("/services/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> updateService(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        jdbcTemplate.update("""
                UPDATE services SET name = ?, description = ?, duration_minutes = ?, price = ?, active = ? WHERE id_service = ?
                """, text(body, "name"), nullable(text(body, "description")), numberOrDefault(body, "durationMinutes", 30), decimalText(body, "price"), active(body), id);
        return jdbcTemplate.queryForMap("SELECT id_service id, name, description, duration_minutes durationMinutes, price, active FROM services WHERE id_service = ?", id);
    }

    @GetMapping("/specialist-services")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA')")
    public List<Map<String, Object>> specialistServices() {
        return jdbcTemplate.queryForList("""
                SELECT ss.id_specialist specialistId, ss.id_service serviceId, sv.name serviceName
                FROM specialist_services ss
                INNER JOIN services sv ON sv.id_service = ss.id_service
                ORDER BY ss.id_specialist, sv.name
                """);
    }

    @PostMapping("/specialist-services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignService(@RequestBody Map<String, Object> body) {
        jdbcTemplate.update("INSERT IGNORE INTO specialist_services (id_specialist, id_service) VALUES (?, ?)", number(body, "specialistId"), number(body, "serviceId"));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/specialist-schedules")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA')")
    public List<Map<String, Object>> schedules() {
        return jdbcTemplate.queryForList("SELECT id_schedule id, id_specialist specialistId, day_of_week dayOfWeek, start_time startTime, end_time endTime, active FROM specialist_schedules ORDER BY id_specialist, day_of_week, start_time");
    }

    @PostMapping("/specialist-schedules")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> createSchedule(@RequestBody Map<String, Object> body) {
        jdbcTemplate.update("""
                INSERT INTO specialist_schedules (id_specialist, day_of_week, start_time, end_time, active)
                VALUES (?, ?, ?, ?, ?)
                """, number(body, "specialistId"), number(body, "dayOfWeek"), text(body, "startTime"), text(body, "endTime"), active(body));
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return jdbcTemplate.queryForMap("SELECT id_schedule id, id_specialist specialistId, day_of_week dayOfWeek, start_time startTime, end_time endTime, active FROM specialist_schedules WHERE id_schedule = ?", id);
    }

    private String[] splitName(String name) {
        String clean = name == null ? "" : name.trim();
        if (clean.isBlank()) return new String[]{"Especialista", ""};
        String[] parts = clean.split("\\s+", 2);
        return new String[]{parts[0], parts.length > 1 ? parts[1] : ""};
    }

    private String text(Map<String, Object> body, String key) {
        Object value = body.get(key);
        return value == null ? "" : String.valueOf(value).trim();
    }

    private String nullable(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private Long number(Map<String, Object> body, String key) {
        Object value = body.get(key);
        if (value instanceof Number number) return number.longValue();
        return Long.parseLong(String.valueOf(value));
    }

    private Long optionalNumber(Map<String, Object> body, String key) {
        Object value = body.get(key);
        if (value == null || String.valueOf(value).isBlank()) return null;
        if (value instanceof Number number) return number.longValue();
        return Long.parseLong(String.valueOf(value));
    }

    private Long numberOrDefault(Map<String, Object> body, String key, long fallback) {
        Object value = body.get(key);
        if (value == null || String.valueOf(value).isBlank()) return fallback;
        if (value instanceof Number number) return number.longValue();
        return Long.parseLong(String.valueOf(value));
    }

    private String decimalText(Map<String, Object> body, String key) {
        String value = text(body, key);
        return value.isBlank() ? null : value;
    }

    private boolean active(Map<String, Object> body) {
        String status = text(body, "status").toUpperCase(Locale.ROOT);
        if (status.startsWith("INACT")) return false;
        Object active = body.get("active");
        return active == null || Boolean.parseBoolean(String.valueOf(active));
    }
}