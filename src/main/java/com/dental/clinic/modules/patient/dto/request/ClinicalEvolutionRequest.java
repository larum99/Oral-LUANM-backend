package com.dental.clinic.modules.patient.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClinicalEvolutionRequest(
        @NotNull(message = "La historia clinica es obligatoria")
        Long medicalHistoryId,
        Long appointmentId,
        @NotNull(message = "El especialista es obligatorio")
        Long specialistId,
        @NotBlank(message = "El diagnostico es obligatorio")
        String diagnosis,
        String treatment,
        String notes
) {
}