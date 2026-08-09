package com.dental.clinic.modules.auth.dto.response;

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
        Long patientId
) {
}
