package com.dental.clinic.modules.patient.mapper;

import com.dental.clinic.modules.patient.dto.response.PatientDirectoryResponse;
import com.dental.clinic.modules.patient.entity.Patient;
import com.dental.clinic.modules.auth.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PatientDirectoryMapper {
    public PatientDirectoryResponse toResponse(Patient patient) {
        User user = patient.getUser();
        return new PatientDirectoryResponse(
                patient.getId(),
                user == null ? null : user.getId(),
                patient.getDocumentType(),
                patient.getDocumentNumber(),
                patient.getBirthDate(),
                displayName(user),
                user == null ? null : user.getEmail(),
                user == null ? null : user.getPhone(),
                user == null ? null : user.getStatus(),
                "Cliente"
        );
    }

    private String displayName(User user) {
        if (user == null) return null;
        String first = user.getFirstname() == null ? "" : user.getFirstname().trim();
        String last = user.getLastName() == null ? "" : user.getLastName().trim();
        String full = (first + " " + last).trim();
        return full.isBlank() ? user.getEmail() : full;
    }
}
