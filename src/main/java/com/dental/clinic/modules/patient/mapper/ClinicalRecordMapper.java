package com.dental.clinic.modules.patient.mapper;

import com.dental.clinic.modules.appointment.dto.response.AppointmentHistoryResponse;
import com.dental.clinic.modules.patient.dto.response.ClinicalEvolutionResponse;
import com.dental.clinic.modules.patient.dto.response.MedicalHistoryResponse;
import com.dental.clinic.modules.appointment.entity.AppointmentHistory;
import com.dental.clinic.modules.patient.entity.ClinicalEvolution;
import com.dental.clinic.modules.patient.entity.MedicalHistory;

public final class ClinicalRecordMapper {

    private ClinicalRecordMapper() {
    }

    public static AppointmentHistoryResponse toAppointmentHistoryResponse(AppointmentHistory history) {
        return new AppointmentHistoryResponse(
                history.getId(),
                history.getAppointment().getId(),
                history.getPreviousStatus() == null ? null : history.getPreviousStatus().name(),
                history.getNewStatus().name(),
                history.getComment(),
                history.getChangedBy() == null ? null : history.getChangedBy().getId(),
                history.getChangedAt()
        );
    }

    public static MedicalHistoryResponse toMedicalHistoryResponse(MedicalHistory history) {
        return new MedicalHistoryResponse(
                history.getId(),
                history.getPatient().getId(),
                history.getMedicalHistory(),
                history.getAllergies(),
                history.getNotes(),
                history.getCreatedAt(),
                history.getUpdatedAt()
        );
    }

    public static ClinicalEvolutionResponse toClinicalEvolutionResponse(ClinicalEvolution evolution) {
        return new ClinicalEvolutionResponse(
                evolution.getId(),
                evolution.getMedicalHistory().getId(),
                evolution.getAppointment() == null ? null : evolution.getAppointment().getId(),
                evolution.getSpecialist().getId(),
                evolution.getDiagnosis(),
                evolution.getTreatment(),
                evolution.getNotes(),
                evolution.getRegisteredAt()
        );
    }
}