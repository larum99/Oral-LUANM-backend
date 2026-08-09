package com.dental.clinic.modules.patient.service;

import com.dental.clinic.modules.patient.dto.request.ClinicalEvolutionRequest;
import com.dental.clinic.modules.patient.dto.request.MedicalHistoryRequest;
import com.dental.clinic.modules.appointment.dto.response.AppointmentHistoryResponse;
import com.dental.clinic.modules.patient.dto.response.ClinicalEvolutionResponse;
import com.dental.clinic.modules.patient.dto.response.MedicalHistoryResponse;

import java.util.List;

public interface ClinicalRecordService {
    List<AppointmentHistoryResponse> listAppointmentHistory();
    List<MedicalHistoryResponse> listMedicalHistories();
    MedicalHistoryResponse upsertMedicalHistory(MedicalHistoryRequest request);
    List<ClinicalEvolutionResponse> listClinicalEvolutions();
    ClinicalEvolutionResponse createClinicalEvolution(ClinicalEvolutionRequest request);
}