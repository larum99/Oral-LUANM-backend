package com.dental.clinic.modules.doctor.service.impl;

import com.dental.clinic.modules.doctor.dto.request.DentalServiceRequest;
import com.dental.clinic.modules.doctor.dto.request.SpecialistRequest;
import com.dental.clinic.modules.doctor.dto.request.SpecialistScheduleRequest;
import com.dental.clinic.modules.doctor.dto.response.SpecialistResponse;
import com.dental.clinic.modules.auth.entity.Role;
import com.dental.clinic.shared.exception.BusinessException;
import com.dental.clinic.shared.exception.DuplicateResourceException;
import com.dental.clinic.modules.auth.repository.RoleRepository;
import com.dental.clinic.modules.doctor.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CatalogServiceImplTest {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private RoleRepository roleRepository;

    @Test
    void rejectsDuplicatedServiceName() {
        DentalServiceRequest request = serviceRequest("Limpieza dental");
        catalogService.createService(request);

        assertThrows(DuplicateResourceException.class, () -> catalogService.createService(serviceRequest("limpieza dental")));
    }

    @Test
    void rejectsOverlappingSpecialistSchedule() {
        ensureSpecialistRole();
        SpecialistResponse specialist = catalogService.createSpecialist(specialistRequest());
        SpecialistScheduleRequest first = new SpecialistScheduleRequest(specialist.id(), (short) 2, LocalTime.of(8, 0), LocalTime.of(12, 0), true);
        SpecialistScheduleRequest second = new SpecialistScheduleRequest(specialist.id(), (short) 2, LocalTime.of(11, 0), LocalTime.of(13, 0), true);

        assertNotNull(catalogService.createSchedule(first).id());
        assertThrows(BusinessException.class, () -> catalogService.createSchedule(second));
    }

    private DentalServiceRequest serviceRequest(String name) {
        DentalServiceRequest request = new DentalServiceRequest();
        request.setName(name);
        request.setDescription("Servicio de prueba");
        request.setDurationMinutes((short) 30);
        request.setActive(true);
        return request;
    }

    private SpecialistRequest specialistRequest() {
        SpecialistRequest request = new SpecialistRequest();
        request.setName("Especialista Test");
        request.setEmail("catalog-specialist-%s@test.com".formatted(System.nanoTime()));
        request.setPassword("12345678");
        request.setPhone("3001234567");
        request.setSpecialty("Ortodoncia");
        request.setProfessionalLicense("CAT-%s".formatted(System.nanoTime()));
        request.setActive(true);
        return request;
    }

    private void ensureSpecialistRole() {
        roleRepository.findByName("ESPECIALISTA").orElseGet(() -> {
            Role role = new Role();
            role.setName("ESPECIALISTA");
            role.setDescription("Especialista");
            return roleRepository.save(role);
        });
    }
}
