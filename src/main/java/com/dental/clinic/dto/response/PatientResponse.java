package com.dental.clinic.dto.response;

import java.time.LocalDate;

public record PatientResponse(
        Long id,
        Long userId,
        String documentType,
        String documentNumber,
        LocalDate birthDate,
        Boolean acceptsData,
        Boolean acceptsPromotions
) {
}