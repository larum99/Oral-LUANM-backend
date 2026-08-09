package com.dental.clinic.modules.doctor.dto.response;

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
