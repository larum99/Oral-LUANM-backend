package com.dental.clinic.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record DentalServiceRequest(
        @NotBlank
        @Size(max = 100)
        String name,
        String description,
        @NotNull
        @Positive
        Short durationMinutes,
        @PositiveOrZero
        BigDecimal price,
        @NotNull
        Boolean active
) {
}