package com.dental.clinic.modules.appointment.dto.response;

import java.time.LocalDateTime;

public record AppointmentHistoryResponse(
        Long id,
        Long appointmentId,
        String previousStatus,
        String newStatus,
        String comment,
        Long changedBy,
        LocalDateTime changedAt
) {
}