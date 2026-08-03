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
public class UsersRolesController {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsersRolesController(JdbcTemplate jdbcTemplate, BCryptPasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> users() {
        return jdbcTemplate.queryForList("""
                SELECT u.id_user id, u.first_name firstName, u.last_name lastName,
                       TRIM(CONCAT(u.first_name, ' ', COALESCE(u.last_name, ''))) name,
                       u.email, u.phone, u.status, r.id_role roleId, r.name role, p.id_patient patientId
                FROM users u
                INNER JOIN roles r ON r.id_role = u.id_role
                LEFT JOIN patients p ON p.id_user = u.id_user
                ORDER BY u.id_user
                """);
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> createUser(@RequestBody Map<String, Object> body) {
        String email = text(body, "email").toLowerCase(Locale.ROOT);
        String password = text(body, "password");
        if (password.length() < 8) {
            throw new IllegalArgumentException("La contrasena del usuario es obligatoria y debe tener minimo 8 caracteres.");
        }
        Long roleId = roleId(text(body, "role"));
        String[] names = splitName(text(body, "name"));
        jdbcTemplate.update("""
                INSERT INTO users (id_role, first_name, last_name, email, phone, password_hash, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """, roleId, nonBlank(text(body, "firstName"), names[0]), nonBlank(text(body, "lastName"), names[1]),
                email, nullable(text(body, "phone")), passwordEncoder.encode(password), dbStatus(text(body, "status")));
        Long id = jdbcTemplate.queryForObject("SELECT id_user FROM users WHERE email = ?", Long.class, email);
        createPatientIfNeeded(id, text(body, "role"), text(body, "documentNumber", text(body, "patientDocument")));
        return jdbcTemplate.queryForMap("SELECT id_user id, email, first_name firstName, last_name lastName, status FROM users WHERE id_user = ?", id);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String[] names = splitName(text(body, "name"));
        jdbcTemplate.update("""
                UPDATE users
                SET id_role = ?, first_name = ?, last_name = ?, email = ?, phone = ?, status = ?
                WHERE id_user = ?
                """, roleId(text(body, "role")), nonBlank(text(body, "firstName"), names[0]), nonBlank(text(body, "lastName"), names[1]),
                text(body, "email").toLowerCase(Locale.ROOT), nullable(text(body, "phone")), dbStatus(text(body, "status")), id);
        if (!text(body, "password").isBlank()) {
            if (text(body, "password").length() < 8) throw new IllegalArgumentException("La contrasena debe tener minimo 8 caracteres.");
            jdbcTemplate.update("UPDATE users SET password_hash = ? WHERE id_user = ?", passwordEncoder.encode(text(body, "password")), id);
        }
        createPatientIfNeeded(id, text(body, "role"), text(body, "documentNumber", text(body, "patientDocument")));
        return jdbcTemplate.queryForMap("SELECT id_user id, email, first_name firstName, last_name lastName, status FROM users WHERE id_user = ?", id);
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> roles() {
        return jdbcTemplate.queryForList("SELECT id_role id, name, description FROM roles ORDER BY id_role");
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> permissions() {
        return jdbcTemplate.queryForList("SELECT id_permission id, code, description FROM permissions ORDER BY code");
    }

    @GetMapping("/role-permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> rolePermissions() {
        return jdbcTemplate.queryForList("""
                SELECT r.id_role roleId, r.name role, p.id_permission permissionId, p.code permission
                FROM role_permissions rp
                INNER JOIN roles r ON r.id_role = rp.id_role
                INNER JOIN permissions p ON p.id_permission = rp.id_permission
                ORDER BY r.name, p.code
                """);
    }

    @PutMapping("/roles/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateRolePermissions(@PathVariable Long roleId, @RequestBody Map<String, Object> body) {
        jdbcTemplate.update("DELETE FROM role_permissions WHERE id_role = ?", roleId);
        Object raw = body.get("permissions");
        if (raw instanceof List<?> permissions) {
            permissions.forEach(permission -> jdbcTemplate.update(
                    "INSERT IGNORE INTO role_permissions (id_role, id_permission) SELECT ?, id_permission FROM permissions WHERE code = ?",
                    roleId, String.valueOf(permission)));
        }
        return ResponseEntity.noContent().build();
    }

    private void createPatientIfNeeded(Long userId, String role, String documentNumber) {
        String normalized = role.trim().toUpperCase(Locale.ROOT);
        if (!(normalized.equals("CLIENTE") || normalized.equals("PACIENTE") || normalized.equals("CLIENT"))) return;
        Integer exists = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM patients WHERE id_user = ?", Integer.class, userId);
        if (exists != null && exists > 0) return;
        String document = documentNumber == null || documentNumber.isBlank() ? "AUTO-" + userId : documentNumber;
        jdbcTemplate.update("INSERT INTO patients (id_user, document_type, document_number, accepts_data, accepts_promotions) VALUES (?, ?, ?, ?, ?)", userId, "CC", document, true, false);
    }

    private Long roleId(String role) {
        String normalized = role.trim().toUpperCase(Locale.ROOT);
        String dbRole = switch (normalized) {
            case "ADMIN", "ADMINISTRADOR" -> "ADMIN";
            case "SECRETARIO", "SECRETARIA", "SECRETARY" -> "SECRETARIO";
            case "ESPECIALISTA", "SPECIALIST" -> "ESPECIALISTA";
            case "AUXILIAR" -> "AUXILIAR";
            default -> "PACIENTE";
        };
        return jdbcTemplate.queryForObject("SELECT id_role FROM roles WHERE name = ?", Long.class, dbRole);
    }

    private String dbStatus(String status) {
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (normalized.startsWith("INACT")) return "INACTIVO";
        if (normalized.startsWith("BLOQ")) return "BLOQUEADO";
        return "ACTIVO";
    }

    private String[] splitName(String name) {
        String clean = name == null ? "" : name.trim();
        if (clean.isBlank()) return new String[]{"Usuario", ""};
        String[] parts = clean.split("\\s+", 2);
        return new String[]{parts[0], parts.length > 1 ? parts[1] : ""};
    }

    private String text(Map<String, Object> body, String key) {
        Object value = body.get(key);
        return value == null ? "" : String.valueOf(value).trim();
    }

    private String text(Map<String, Object> body, String key, String fallback) {
        String value = text(body, key);
        return value.isBlank() ? fallback : value;
    }

    private String nonBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String nullable(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}