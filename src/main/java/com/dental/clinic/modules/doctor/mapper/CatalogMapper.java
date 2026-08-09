package com.dental.clinic.modules.doctor.mapper;

import com.dental.clinic.modules.doctor.dto.response.DentalServiceResponse;
import com.dental.clinic.modules.doctor.dto.response.SpecialistResponse;
import com.dental.clinic.modules.doctor.dto.response.SpecialistScheduleResponse;
import com.dental.clinic.modules.doctor.dto.response.SpecialistServiceAssignmentResponse;
import com.dental.clinic.modules.doctor.entity.DentalService;
import com.dental.clinic.modules.doctor.entity.Specialist;
import com.dental.clinic.modules.doctor.entity.SpecialistSchedule;
import com.dental.clinic.modules.auth.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CatalogMapper {
    public SpecialistResponse toSpecialistResponse(Specialist specialist) {
        User user = specialist.getUser();
        return new SpecialistResponse(
                specialist.getId(),
                user == null ? null : user.getId(),
                displayName(user),
                user == null ? null : user.getEmail(),
                user == null ? null : user.getPhone(),
                specialist.getSpecialty(),
                specialist.getProfessionalLicense(),
                specialist.getActive(),
                Boolean.TRUE.equals(specialist.getActive()) ? "Activo" : "Inactivo"
        );
    }

    public DentalServiceResponse toDentalServiceResponse(DentalService service) {
        return new DentalServiceResponse(service.getId(), service.getName(), service.getDescription(), service.getDurationMinutes(), service.getPrice(), service.getActive());
    }

    public SpecialistScheduleResponse toScheduleResponse(SpecialistSchedule schedule) {
        return new SpecialistScheduleResponse(schedule.getId(), schedule.getSpecialist().getId(), schedule.getDayOfWeek(), schedule.getStartTime(), schedule.getEndTime(), schedule.getActive());
    }

    public SpecialistServiceAssignmentResponse toAssignmentResponse(Long specialistId, DentalService service) {
        return new SpecialistServiceAssignmentResponse(specialistId, service.getId(), service.getName());
    }

    private String displayName(User user) {
        if (user == null) return null;
        String first = user.getFirstname() == null ? "" : user.getFirstname().trim();
        String last = user.getLastName() == null ? "" : user.getLastName().trim();
        String fullName = (first + " " + last).trim();
        return fullName.isBlank() ? user.getEmail() : fullName;
    }
}
