package com.dental.clinic.modules.patient.service.impl;

import com.dental.clinic.modules.patient.dto.request.ClinicalEvolutionRequest;
import com.dental.clinic.modules.patient.dto.request.MedicalHistoryRequest;
import com.dental.clinic.modules.appointment.dto.response.AppointmentHistoryResponse;
import com.dental.clinic.modules.patient.dto.response.ClinicalEvolutionResponse;
import com.dental.clinic.modules.patient.dto.response.MedicalHistoryResponse;
import com.dental.clinic.modules.appointment.entity.Appointment;
import com.dental.clinic.modules.patient.entity.ClinicalEvolution;
import com.dental.clinic.modules.patient.entity.MedicalHistory;
import com.dental.clinic.modules.patient.entity.Patient;
import com.dental.clinic.modules.doctor.entity.Specialist;
import com.dental.clinic.modules.patient.mapper.ClinicalRecordMapper;
import com.dental.clinic.modules.appointment.repository.AppointmentHistoryRepository;
import com.dental.clinic.modules.appointment.repository.AppointmentRepository;
import com.dental.clinic.modules.patient.repository.ClinicalEvolutionRepository;
import com.dental.clinic.modules.patient.repository.MedicalHistoryRepository;
import com.dental.clinic.modules.patient.repository.PatientRepository;
import com.dental.clinic.modules.doctor.repository.SpecialistRepository;
import com.dental.clinic.modules.patient.service.ClinicalRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClinicalRecordServiceImpl implements ClinicalRecordService {

    private final AppointmentHistoryRepository appointmentHistoryRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final ClinicalEvolutionRepository clinicalEvolutionRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final SpecialistRepository specialistRepository;

    public ClinicalRecordServiceImpl(AppointmentHistoryRepository appointmentHistoryRepository,
                                     MedicalHistoryRepository medicalHistoryRepository,
                                     ClinicalEvolutionRepository clinicalEvolutionRepository,
                                     PatientRepository patientRepository,
                                     AppointmentRepository appointmentRepository,
                                     SpecialistRepository specialistRepository) {
        this.appointmentHistoryRepository = appointmentHistoryRepository;
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.clinicalEvolutionRepository = clinicalEvolutionRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.specialistRepository = specialistRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentHistoryResponse> listAppointmentHistory() {
        return appointmentHistoryRepository.findAllDetailed().stream()
                .map(ClinicalRecordMapper::toAppointmentHistoryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalHistoryResponse> listMedicalHistories() {
        return medicalHistoryRepository.findAllDetailed().stream()
                .map(ClinicalRecordMapper::toMedicalHistoryResponse)
                .toList();
    }

    @Override
    public MedicalHistoryResponse upsertMedicalHistory(MedicalHistoryRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado."));
        MedicalHistory history = medicalHistoryRepository.findByPatientId(patient.getId())
                .orElseGet(MedicalHistory::new);
        history.setPatient(patient);
        history.setMedicalHistory(nullable(request.medicalHistory()));
        history.setAllergies(nullable(request.allergies()));
        history.setNotes(nullable(request.notes()));
        MedicalHistory saved = medicalHistoryRepository.save(history);
        return ClinicalRecordMapper.toMedicalHistoryResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicalEvolutionResponse> listClinicalEvolutions() {
        return clinicalEvolutionRepository.findAllDetailed().stream()
                .map(ClinicalRecordMapper::toClinicalEvolutionResponse)
                .toList();
    }

    @Override
    public ClinicalEvolutionResponse createClinicalEvolution(ClinicalEvolutionRequest request) {
        MedicalHistory medicalHistory = medicalHistoryRepository.findDetailedById(request.medicalHistoryId())
                .orElseThrow(() -> new IllegalArgumentException("Historia clinica no encontrada."));
        Specialist specialist = specialistRepository.findById(request.specialistId())
                .orElseThrow(() -> new IllegalArgumentException("Especialista no encontrado."));
        Appointment appointment = null;
        if (request.appointmentId() != null) {
            appointment = appointmentRepository.findById(request.appointmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada."));
        }
        ClinicalEvolution evolution = new ClinicalEvolution();
        evolution.setMedicalHistory(medicalHistory);
        evolution.setAppointment(appointment);
        evolution.setSpecialist(specialist);
        evolution.setDiagnosis(nonBlank(request.diagnosis(), "El diagnostico es obligatorio."));
        evolution.setTreatment(nullable(request.treatment()));
        evolution.setNotes(nullable(request.notes()));
        ClinicalEvolution saved = clinicalEvolutionRepository.save(evolution);
        return ClinicalRecordMapper.toClinicalEvolutionResponse(saved);
    }

    private String nonBlank(String value, String message) {
        String clean = value == null ? "" : value.trim();
        if (clean.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return clean;
    }

    private String nullable(String value) {
        String clean = value == null ? "" : value.trim();
        return clean.isBlank() ? null : clean;
    }
}