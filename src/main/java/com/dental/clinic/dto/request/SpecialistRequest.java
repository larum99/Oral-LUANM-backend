package com.dental.clinic.dto.request;

import jakarta.validation.constraints.*;

import java.util.Set;

public record SpecialistRequest(
        @NotNull
        Long userId,
        @NotBlank
        @Size(max = 100)
        String specialty,
        @Size(max = 50)
        String professionalLicense,
        @NotNull
        Boolean active,
        @NotNull
        Set<Long> serviceIds
) {
}