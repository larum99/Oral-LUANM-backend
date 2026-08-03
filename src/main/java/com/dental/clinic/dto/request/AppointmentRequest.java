package com.dental.clinic.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull
        Long patientId,
        @NotNull
        Long specialistId,
        @NotNull
        Long serviceId,
        @NotNull
        @Future
        LocalDateTime startDatetime,
        @NotBlank
        @Size(max = 150)
        String reason,
        @Size(max = 500)
        String notes
) {
}