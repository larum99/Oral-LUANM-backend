package com.dental.clinic.modules.appointment.mapper;

import com.dental.clinic.modules.appointment.dto.response.AppointmentResponse;
import com.dental.clinic.modules.appointment.entity.Appointment;
import com.dental.clinic.modules.doctor.entity.DentalService;
import com.dental.clinic.modules.patient.entity.Patient;
import com.dental.clinic.modules.doctor.entity.Specialist;
import com.dental.clinic.modules.auth.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentResponse toResponse(Appointment appointment) {
        Patient patient = appointment.getPatient();
        User patientUser = patient.getUser();
        Specialist specialist = appointment.getSpecialist();
        User specialistUser = specialist.getUser();
        DentalService service = appointment.getService();
        User createdBy = appointment.getCreatedBy();

        return new AppointmentResponse(
                appointment.getId(),
                patient.getId(),
                specialist.getId(),
                service == null ? null : service.getId(),
                appointment.getStartDatetime(),
                appointment.getStartDatetime().toLocalDate(),
                appointment.getStartDatetime().toLocalTime().toString().substring(0, 5),
                appointment.getEndDatetime(),
                appointment.getReason(),
                appointment.getNotes(),
                appointment.getStatus(),
                patientUser == null ? null : patientUser.getEmail(),
                displayName(patientUser),
                specialistUser == null ? null : specialistUser.getEmail(),
                displayName(specialistUser),
                specialist.getSpecialty(),
                service == null ? null : service.getName(),
                createdBy == null ? null : createdBy.getId(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }

    private String displayName(User user) {
        if (user == null) return null;
        String firstName = user.getFirstname() == null ? "" : user.getFirstname().trim();
        String lastName = user.getLastName() == null ? "" : user.getLastName().trim();
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isBlank() ? user.getEmail() : fullName;
    }
}
