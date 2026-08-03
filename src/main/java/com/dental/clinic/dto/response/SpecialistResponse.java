package com.dental.clinic.dto.response;

import java.util.Set;

public record SpecialistResponse(
        Long id,
        Long userId,
        String specialty,
        String professionalLicense,
        Boolean active,
        Set<Long> serviceIds

) {
}