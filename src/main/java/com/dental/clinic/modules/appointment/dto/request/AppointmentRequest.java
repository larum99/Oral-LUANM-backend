package com.dental.clinic.modules.appointment.dto.request;

import com.dental.clinic.modules.appointment.util.AppointmentStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Locale;

public class AppointmentRequest {

    private Long patientId;

    @NotNull
    private Long specialistId;

    private Long serviceId;
    private String startDatetime;
    private String date;
    private String time;

    @NotBlank
    @Size(max = 150)
    private String reason;

    @Size(max = 500)
    private String notes;

    private String status;

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getSpecialistId() { return specialistId; }
    public void setSpecialistId(Long specialistId) { this.specialistId = specialistId; }
    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
    public String getStartDatetime() { return startDatetime; }
    public void setStartDatetime(String startDatetime) { this.startDatetime = startDatetime; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @AssertTrue(message = "Debe enviar startDatetime o date y time.")
    public boolean hasStartDatetime() {
        return (startDatetime != null && !startDatetime.isBlank())
                || (date != null && !date.isBlank() && time != null && !time.isBlank());
    }

    public LocalDateTime resolvedStartDatetime() {
        String value = startDatetime != null && !startDatetime.isBlank()
                ? startDatetime.trim()
                : date.trim() + "T" + time.trim();
        return LocalDateTime.parse(value);
    }

    public AppointmentStatus resolvedStatus() {
        if (status == null || status.isBlank()) {
            return AppointmentStatus.PENDIENTE;
        }
        String normalized = status.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        return switch (normalized) {
            case "CONFIRMADA" -> AppointmentStatus.CONFIRMADA;
            case "ATENDIDA" -> AppointmentStatus.ATENDIDA;
            case "CANCELADA" -> AppointmentStatus.CANCELADA;
            case "NO_ASISTIO" -> AppointmentStatus.NO_ASISTIO;
            default -> AppointmentStatus.PENDIENTE;
        };
    }
}
