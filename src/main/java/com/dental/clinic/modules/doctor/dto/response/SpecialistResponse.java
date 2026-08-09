package com.dental.clinic.modules.doctor.dto.response;

public record SpecialistResponse(
        Long id,
        Long userId,
        String name,
        String email,
        String phone,
        String specialty,
        String professionalLicense,
        Boolean active,
        String status
) {
}
