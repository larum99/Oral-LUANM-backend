package com.dental.clinic.modules.patient.dto.request;

import jakarta.validation.constraints.NotNull;

public record MedicalHistoryRequest(
        @NotNull(message = "El paciente es obligatorio")
        Long patientId,
        String medicalHistory,
        String allergies,
        String notes
) {
}