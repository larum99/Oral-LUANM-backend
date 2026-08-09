package com.dental.clinic.modules.patient.service.impl;

import com.dental.clinic.modules.patient.dto.request.ClinicalEvolutionRequest;
import com.dental.clinic.modules.patient.dto.request.MedicalHistoryRequest;
import com.dental.clinic.modules.patient.dto.response.ClinicalEvolutionResponse;
import com.dental.clinic.modules.patient.dto.response.MedicalHistoryResponse;
import com.dental.clinic.modules.patient.entity.Patient;
import com.dental.clinic.modules.auth.entity.Role;
import com.dental.clinic.modules.doctor.entity.Specialist;
import com.dental.clinic.modules.auth.entity.User;
import com.dental.clinic.modules.patient.repository.PatientRepository;
import com.dental.clinic.modules.auth.repository.RoleRepository;
import com.dental.clinic.modules.doctor.repository.SpecialistRepository;
import com.dental.clinic.modules.auth.repository.UserRepository;
import com.dental.clinic.modules.patient.service.ClinicalRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ClinicalRecordServiceImplTest {

    @Autowired
    private ClinicalRecordService clinicalRecordService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private SpecialistRepository specialistRepository;

    @Test
    void upsertsSingleMedicalHistoryPerPatient() {
        Patient patient = patient();

        MedicalHistoryResponse first = clinicalRecordService.upsertMedicalHistory(
                new MedicalHistoryRequest(patient.getId(), "Antecedente inicial", "Ninguna", "Nota inicial"));
        MedicalHistoryResponse second = clinicalRecordService.upsertMedicalHistory(
                new MedicalHistoryRequest(patient.getId(), "Antecedente actualizado", "Penicilina", "Nota actualizada"));

        assertNotNull(first.id());
        assertEquals(first.id(), second.id());
        assertEquals("Penicilina", second.allergies());
    }

    @Test
    void createsClinicalEvolutionForExistingHistoryAndSpecialist() {
        Patient patient = patient();
        Specialist specialist = specialist();
        MedicalHistoryResponse history = clinicalRecordService.upsertMedicalHistory(
                new MedicalHistoryRequest(patient.getId(), "Sin antecedentes", null, null));

        ClinicalEvolutionResponse evolution = clinicalRecordService.createClinicalEvolution(
                new ClinicalEvolutionRequest(history.id(), null, specialist.getId(), "Caries incipiente", "Restauracion", "Control en 6 meses"));

        assertNotNull(evolution.id());
        assertEquals(history.id(), evolution.medicalHistoryId());
        assertEquals(specialist.getId(), evolution.specialistId());
        assertThrows(IllegalArgumentException.class,
                () -> clinicalRecordService.createClinicalEvolution(new ClinicalEvolutionRequest(999999L, null, specialist.getId(), "Dx", null, null)));
    }

    private Patient patient() {
        User user = user("patient-clinical-%s@test.com".formatted(System.nanoTime()), role("PACIENTE"));
        Patient patient = new Patient();
        patient.setUser(user);
        patient.setDocumentType("CC");
        patient.setDocumentNumber("CLIN-PAT-%s".formatted(System.nanoTime()));
        patient.setAcceptsData(true);
        patient.setAcceptsPromotions(false);
        return patientRepository.save(patient);
    }

    private Specialist specialist() {
        User user = user("specialist-clinical-%s@test.com".formatted(System.nanoTime()), role("ESPECIALISTA"));
        Specialist specialist = new Specialist();
        specialist.setUser(user);
        specialist.setSpecialty("Endodoncia");
        specialist.setProfessionalLicense("CLIN-LIC-%s".formatted(System.nanoTime()));
        specialist.setActive(true);
        return specialistRepository.save(specialist);
    }

    private User user(String email, Role role) {
        User user = new User();
        user.setRole(role);
        user.setFirstname("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setPhone("3001234567");
        user.setPasswordHash("hash");
        user.setStatus("ACTIVO");
        return userRepository.save(user);
    }

    private Role role(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setDescription(name);
            return roleRepository.save(role);
        });
    }
}