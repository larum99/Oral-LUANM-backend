package com.dental.clinic.mapper;

import com.dental.clinic.dto.request.AppointmentRequest;
import com.dental.clinic.dto.response.AppointmentResponse;
import com.dental.clinic.entity.Appointment;
import com.dental.clinic.entity.DentalService;
import com.dental.clinic.entity.Patient;
import com.dental.clinic.entity.Specialist;
import com.dental.clinic.entity.User;
import com.dental.clinic.utils.AppointmentStatus;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public Appointment toEntity(
            AppointmentRequest request,
            Patient patient,
            Specialist specialist,
            DentalService service,
            User createdBy
    ) {

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setSpecialist(specialist);
        appointment.setService(service);
        appointment.setCreatedBy(createdBy);

        appointment.setStartDatetime(request.startDatetime());

        appointment.setReason(request.reason());
        appointment.setNotes(request.notes());

        appointment.setStatus(AppointmentStatus.PENDIENTE);

        return appointment;
    }

    public AppointmentResponse toResponse(Appointment appointment) {

        Long createdById = appointment.getCreatedBy() != null
                ? appointment.getCreatedBy().getId()
                : null;

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getSpecialist().getId(),
                appointment.getService().getId(),
                appointment.getStatus(),
                appointment.getStartDatetime(),
                appointment.getEndDatetime(),
                appointment.getReason(),
                appointment.getNotes(),
                createdById,
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}