package com.dental.clinic.modules.patient.dto.response;

import java.time.LocalDate;

public record PatientDirectoryResponse(
        Long patientId,
        Long id,
        String documentType,
        String patientDocument,
        LocalDate birthDate,
        String name,
        String email,
        String phone,
        String status,
        String role
) {
}
