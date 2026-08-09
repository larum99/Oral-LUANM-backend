package com.dental.clinic.modules.patient.dto.response;

import java.time.LocalDateTime;

public record ClinicalEvolutionResponse(
        Long id,
        Long medicalHistoryId,
        Long appointmentId,
        Long specialistId,
        String diagnosis,
        String treatment,
        String notes,
        LocalDateTime registeredAt
) {
}