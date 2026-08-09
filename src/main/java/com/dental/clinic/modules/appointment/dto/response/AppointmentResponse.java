package com.dental.clinic.modules.appointment.dto.response;

import com.dental.clinic.modules.appointment.util.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        Long specialistId,
        Long serviceId,
        LocalDateTime startDatetime,
        LocalDate date,
        String time,
        LocalDateTime endDatetime,
        String reason,
        String notes,
        AppointmentStatus status,
        String patientEmail,
        String patientName,
        String specialistEmail,
        String specialistName,
        String specialty,
        String serviceName,
        Long createdById,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
