package com.dental.clinic.mapper;

import com.dental.clinic.dto.request.SpecialistRequest;
import com.dental.clinic.dto.response.SpecialistResponse;
import com.dental.clinic.entity.DentalService;
import com.dental.clinic.entity.Specialist;
import com.dental.clinic.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SpecialistMapper {

    public Specialist toEntity(SpecialistRequest request, User user, Set<DentalService> services) {
        Specialist specialist = new Specialist();

        specialist.setUser(user);
        specialist.setSpecialty(request.specialty());
        specialist.setProfessionalLicense(request.professionalLicense());
        specialist.setActive(request.active());
        specialist.setServices(services);

        return specialist;
    }

    public SpecialistResponse toResponse(Specialist specialist) {

        Long userId = specialist.getUser() != null
                ? specialist.getUser().getId()
                : null;

        Set<Long> serviceIds = specialist.getServices()
                .stream()
                .map(DentalService::getId)
                .collect(Collectors.toSet());

        return new SpecialistResponse(
                specialist.getId(),
                userId,
                specialist.getSpecialty(),
                specialist.getProfessionalLicense(),
                specialist.getActive(),
                serviceIds
        );
    }
}