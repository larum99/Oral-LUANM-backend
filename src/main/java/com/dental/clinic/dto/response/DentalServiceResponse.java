package com.dental.clinic.dto.response;

import java.math.BigDecimal;

public record DentalServiceResponse(
        Long id,
        String name,
        String description,
        Short durationMinutes,
        BigDecimal price,
        Boolean active
) {
}