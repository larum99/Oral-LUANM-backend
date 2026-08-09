package com.dental.clinic.modules.patient.dto.response;

import java.time.LocalDateTime;

public record MedicalHistoryResponse(
        Long id,
        Long patientId,
        String medicalHistory,
        String allergies,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}