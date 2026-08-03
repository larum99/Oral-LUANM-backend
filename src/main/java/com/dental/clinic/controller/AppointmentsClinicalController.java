package com.dental.clinic.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AppointmentsClinicalController {

    private final JdbcTemplate jdbcTemplate;

    public AppointmentsClinicalController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/appointments")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA')")
    public List<Map<String, Object>> appointments() {
        return appointmentRows("");
    }

    @GetMapping("/appointments/my")
    @PreAuthorize("hasRole('PACIENTE')")
    public List<Map<String, Object>> myAppointments(Authentication authentication) {
        Long userId = userId(authentication);
        return jdbcTemplate.queryForList(baseAppointmentSql() + " WHERE p.id_user = ? ORDER BY a.start_datetime DESC", userId);
    }

    @PostMapping("/appointments")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO')")
    public Map<String, Object> createAppointment(@RequestBody Map<String, Object> body, Authentication authentication) {
        return createAppointmentInternal(body, userId(authentication));
    }

    @PostMapping("/appointments/my")
    @PreAuthorize("hasRole('PACIENTE')")
    public Map<String, Object> createMyAppointment(@RequestBody Map<String, Object> body, Authentication authentication) {
        Long currentUserId = userId(authentication);
        Long patientId = jdbcTemplate.query("SELECT id_patient FROM patients WHERE id_user = ?", rs -> rs.next() ? rs.getLong(1) : null, currentUserId);
        if (patientId == null) {
            throw new IllegalArgumentException("El usuario autenticado no tiene paciente asociado.");
        }
        body.put("patientId", patientId);
        return createAppointmentInternal(body, currentUserId);
    }

    @PutMapping("/appointments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO')")
    public Map<String, Object> updateAppointment(@PathVariable Long id, @RequestBody Map<String, Object> body, Authentication authentication) {
        String previousStatus = jdbcTemplate.queryForObject("SELECT status FROM appointments WHERE id_appointment = ?", String.class, id);
        LocalDateTime start = parseStart(body);
        Long specialistId = number(body, "specialistId");
        if (hasConflict(specialistId, start, id)) {
            throw new IllegalArgumentException("Ya existe una cita para ese especialista en esa fecha y hora.");
        }
        Long serviceId = optionalNumber(body, "serviceId");
        int duration = duration(serviceId);
        String status = dbAppointmentStatus(text(body, "status"));
        jdbcTemplate.update("""
                UPDATE appointments
                SET id_patient = ?, id_specialist = ?, id_service = ?, start_datetime = ?, end_datetime = ?, reason = ?, notes = ?, status = ?
                WHERE id_appointment = ?
                """, number(body, "patientId"), specialistId, serviceId, start, start.plusMinutes(duration), text(body, "reason"), nullable(text(body, "notes")), status, id);
        if (!previousStatus.equals(status)) {
            insertHistory(id, previousStatus, status, "Estado actualizado", userId(authentication));
        }
        return jdbcTemplate.queryForMap("SELECT id_appointment id, status, start_datetime startDatetime, end_datetime endDatetime FROM appointments WHERE id_appointment = ?", id);
    }

    @PatchMapping("/appointments/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA')")
    public Map<String, Object> updateAppointmentStatus(@PathVariable Long id, @RequestBody Map<String, Object> body, Authentication authentication) {
        String previousStatus = jdbcTemplate.queryForObject("SELECT status FROM appointments WHERE id_appointment = ?", String.class, id);
        String status = dbAppointmentStatus(text(body, "status"));
        jdbcTemplate.update("UPDATE appointments SET status = ? WHERE id_appointment = ?", status, id);
        insertHistory(id, previousStatus, status, text(body, "comment", "Estado actualizado"), userId(authentication));
        return jdbcTemplate.queryForMap("SELECT id_appointment id, status FROM appointments WHERE id_appointment = ?", id);
    }

    @GetMapping("/appointment-history")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA')")
    public List<Map<String, Object>> appointmentHistory() {
        return jdbcTemplate.queryForList("SELECT id_history id, id_appointment appointmentId, previous_status previousStatus, new_status newStatus, comment, changed_by changedBy, changed_at changedAt FROM appointment_history ORDER BY changed_at DESC");
    }

    @GetMapping("/medical-histories")
    @PreAuthorize("hasAnyRole('ADMIN','ESPECIALISTA')")
    public List<Map<String, Object>> medicalHistories() {
        return jdbcTemplate.queryForList("SELECT id_medical_history id, id_patient patientId, medical_history medicalHistory, allergies, notes, created_at createdAt, updated_at updatedAt FROM medical_histories ORDER BY id_medical_history DESC");
    }

    @PostMapping("/medical-histories")
    @PreAuthorize("hasAnyRole('ADMIN','ESPECIALISTA')")
    public Map<String, Object> upsertMedicalHistory(@RequestBody Map<String, Object> body) {
        Long patientId = number(body, "patientId");
        Long existing = jdbcTemplate.query("SELECT id_medical_history FROM medical_histories WHERE id_patient = ?", rs -> rs.next() ? rs.getLong(1) : null, patientId);
        if (existing == null) {
            jdbcTemplate.update("INSERT INTO medical_histories (id_patient, medical_history, allergies, notes) VALUES (?, ?, ?, ?)", patientId, nullable(text(body, "medicalHistory")), nullable(text(body, "allergies")), nullable(text(body, "notes")));
            existing = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        } else {
            jdbcTemplate.update("UPDATE medical_histories SET medical_history = ?, allergies = ?, notes = ? WHERE id_medical_history = ?", nullable(text(body, "medicalHistory")), nullable(text(body, "allergies")), nullable(text(body, "notes")), existing);
        }
        return jdbcTemplate.queryForMap("SELECT id_medical_history id, id_patient patientId, medical_history medicalHistory, allergies, notes FROM medical_histories WHERE id_medical_history = ?", existing);
    }

    @GetMapping("/clinical-evolutions")
    @PreAuthorize("hasAnyRole('ADMIN','ESPECIALISTA')")
    public List<Map<String, Object>> clinicalEvolutions() {
        return jdbcTemplate.queryForList("SELECT id_evolution id, id_medical_history medicalHistoryId, id_appointment appointmentId, id_specialist specialistId, diagnosis, treatment, notes, registered_at registeredAt FROM clinical_evolutions ORDER BY registered_at DESC, id_evolution DESC");
    }

    @PostMapping("/clinical-evolutions")
    @PreAuthorize("hasAnyRole('ADMIN','ESPECIALISTA')")
    public Map<String, Object> createClinicalEvolution(@RequestBody Map<String, Object> body, Authentication authentication) {
        jdbcTemplate.update("""
                INSERT INTO clinical_evolutions (id_medical_history, id_appointment, id_specialist, diagnosis, treatment, notes)
                VALUES (?, ?, ?, ?, ?, ?)
                """, number(body, "medicalHistoryId"), optionalNumber(body, "appointmentId"), number(body, "specialistId"), text(body, "diagnosis"), nullable(text(body, "treatment")), nullable(text(body, "notes")));
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return jdbcTemplate.queryForMap("SELECT id_evolution id, id_medical_history medicalHistoryId, id_appointment appointmentId, id_specialist specialistId, diagnosis, treatment, notes, registered_at registeredAt FROM clinical_evolutions WHERE id_evolution = ?", id);
    }

    private Map<String, Object> createAppointmentInternal(Map<String, Object> body, Long createdBy) {
        LocalDateTime start = parseStart(body);
        Long specialistId = number(body, "specialistId");
        if (hasConflict(specialistId, start, null)) {
            throw new IllegalArgumentException("Ya existe una cita para ese especialista en esa fecha y hora.");
        }
        Long serviceId = optionalNumber(body, "serviceId");
        int duration = duration(serviceId);
        String status = dbAppointmentStatus(text(body, "status"));
        jdbcTemplate.update("""
                INSERT INTO appointments (id_patient, id_specialist, id_service, start_datetime, end_datetime, reason, notes, status, created_by)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, number(body, "patientId"), specialistId, serviceId, start, start.plusMinutes(duration), text(body, "reason"), nullable(text(body, "notes")), status, createdBy);
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        insertHistory(id, null, status, "Cita creada", createdBy);
        return jdbcTemplate.queryForMap("SELECT id_appointment id, status, start_datetime startDatetime, end_datetime endDatetime FROM appointments WHERE id_appointment = ?", id);
    }

    private List<Map<String, Object>> appointmentRows(String where) {
        return jdbcTemplate.queryForList(baseAppointmentSql() + " " + where + " ORDER BY a.start_datetime DESC");
    }

    private String baseAppointmentSql() {
        return """
                SELECT a.id_appointment id, a.id_patient patientId, a.id_specialist specialistId, a.id_service serviceId,
                       a.start_datetime startDatetime, DATE(a.start_datetime) date, TIME_FORMAT(a.start_datetime, '%H:%i') time,
                       a.end_datetime endDatetime, a.reason, a.notes, a.status,
                       pu.email patientEmail, TRIM(CONCAT(pu.first_name, ' ', COALESCE(pu.last_name, ''))) patientName,
                       su.email specialistEmail, TRIM(CONCAT(su.first_name, ' ', COALESCE(su.last_name, ''))) specialistName,
                       s.specialty, sv.name serviceName, a.created_by createdById, a.created_at createdAt
                FROM appointments a
                INNER JOIN patients p ON p.id_patient = a.id_patient
                LEFT JOIN users pu ON pu.id_user = p.id_user
                INNER JOIN specialists s ON s.id_specialist = a.id_specialist
                INNER JOIN users su ON su.id_user = s.id_user
                LEFT JOIN services sv ON sv.id_service = a.id_service
                """;
    }

    private boolean hasConflict(Long specialistId, LocalDateTime start, Long excludeId) {
        Integer count = excludeId == null
                ? jdbcTemplate.queryForObject("SELECT COUNT(*) FROM appointments WHERE id_specialist = ? AND start_datetime = ? AND status <> 'CANCELADA'", Integer.class, specialistId, start)
                : jdbcTemplate.queryForObject("SELECT COUNT(*) FROM appointments WHERE id_specialist = ? AND start_datetime = ? AND id_appointment <> ? AND status <> 'CANCELADA'", Integer.class, specialistId, start, excludeId);
        return count != null && count > 0;
    }

    private int duration(Long serviceId) {
        if (serviceId == null) return 30;
        Integer value = jdbcTemplate.queryForObject("SELECT duration_minutes FROM services WHERE id_service = ?", Integer.class, serviceId);
        return value == null ? 30 : value;
    }

    private LocalDateTime parseStart(Map<String, Object> body) {
        return LocalDateTime.parse(text(body, "startDatetime", text(body, "date") + "T" + text(body, "time")));
    }

    private void insertHistory(Long appointmentId, String previousStatus, String newStatus, String comment, Long changedBy) {
        jdbcTemplate.update("INSERT INTO appointment_history (id_appointment, previous_status, new_status, comment, changed_by) VALUES (?, ?, ?, ?, ?)", appointmentId, previousStatus, newStatus, nullable(comment), changedBy);
    }

    private Long userId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) return null;
        return jdbcTemplate.query("SELECT id_user FROM users WHERE email = ?", rs -> rs.next() ? rs.getLong(1) : null, authentication.getName());
    }

    private String dbAppointmentStatus(String status) {
        String normalized = status.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        return switch (normalized) {
            case "CONFIRMADA", "ATENDIDA", "CANCELADA", "NO_ASISTIO" -> normalized;
            default -> "PENDIENTE";
        };
    }

    private String text(Map<String, Object> body, String key) {
        return text(body, key, "");
    }

    private String text(Map<String, Object> body, String key, String fallback) {
        Object value = body.get(key);
        String clean = value == null ? "" : String.valueOf(value).trim();
        return clean.isBlank() ? fallback : clean;
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
}