package com.dental.clinic.modules.auth.dto.response;

import java.time.LocalDate;

public record UserAdminResponse(
        Long id,
        String firstName,
        String lastName,
        String name,
        String email,
        String phone,
        String status,
        Short roleId,
        String role,
        Long patientId,
        String documentType,
        String patientDocument,
        String documentNumber,
        LocalDate birthDate,
        Boolean acceptsData,
        Boolean acceptsPromotions
) {
}
