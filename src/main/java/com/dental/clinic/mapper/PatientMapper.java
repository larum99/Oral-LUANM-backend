package com.dental.clinic.mapper;

import com.dental.clinic.dto.request.PatientRequest;
import com.dental.clinic.dto.response.PatientResponse;
import com.dental.clinic.entity.Patient;
import com.dental.clinic.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequest request, User user) {
        Patient patient = new Patient();

        patient.setUser(user);
        patient.setDocumentType(request.documentType());
        patient.setDocumentNumber(request.documentNumber());
        patient.setBirthDate(request.birthDate());
        patient.setAcceptsData(request.acceptsData());
        patient.setAcceptsPromotions(request.acceptsPromotions());

        return patient;
    }

    public PatientResponse toResponse(Patient patient) {
        Long userId = patient.getUser() != null
                ? patient.getUser().getId()
                : null;

        return new PatientResponse(
                patient.getId(),
                userId,
                patient.getDocumentType(),
                patient.getDocumentNumber(),
                patient.getBirthDate(),
                patient.getAcceptsData(),
                patient.getAcceptsPromotions()
        );
    }
}