package com.dental.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientRequest(
        @NotNull
        Long userId,
        @NotBlank
        @Size(max = 30)
        String documentType,
        @NotBlank
        @Size(max = 30)
        String documentNumber,
        LocalDate birthDate,
        @NotNull
        Boolean acceptsData,
        @NotNull
        Boolean acceptsPromotions
) {
}
