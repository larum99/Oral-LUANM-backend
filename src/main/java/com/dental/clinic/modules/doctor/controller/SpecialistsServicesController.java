package com.dental.clinic.modules.doctor.controller;

import com.dental.clinic.modules.doctor.dto.request.DentalServiceRequest;
import com.dental.clinic.modules.doctor.dto.request.SpecialistRequest;
import com.dental.clinic.modules.doctor.dto.request.SpecialistScheduleRequest;
import com.dental.clinic.modules.doctor.dto.request.SpecialistServiceAssignmentRequest;
import com.dental.clinic.modules.doctor.dto.response.DentalServiceResponse;
import com.dental.clinic.modules.doctor.dto.response.SpecialistResponse;
import com.dental.clinic.modules.doctor.dto.response.SpecialistScheduleResponse;
import com.dental.clinic.modules.doctor.dto.response.SpecialistServiceAssignmentResponse;
import com.dental.clinic.modules.doctor.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SpecialistsServicesController {

    private final CatalogService catalogService;

    public SpecialistsServicesController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/specialists")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA','PACIENTE')")
    public List<SpecialistResponse> specialists() {
        return catalogService.findSpecialists();
    }

    @PostMapping("/specialists")
    @PreAuthorize("hasRole('ADMIN')")
    public SpecialistResponse createSpecialist(@Valid @RequestBody SpecialistRequest request) {
        return catalogService.createSpecialist(request);
    }

    @PutMapping("/specialists/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SpecialistResponse updateSpecialist(@PathVariable Long id, @Valid @RequestBody SpecialistRequest request) {
        return catalogService.updateSpecialist(id, request);
    }

    @GetMapping("/services")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA','PACIENTE')")
    public List<DentalServiceResponse> services() {
        return catalogService.findServices();
    }

    @PostMapping("/services")
    @PreAuthorize("hasRole('ADMIN')")
    public DentalServiceResponse createService(@Valid @RequestBody DentalServiceRequest request) {
        return catalogService.createService(request);
    }

    @PutMapping("/services/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DentalServiceResponse updateService(@PathVariable Long id, @Valid @RequestBody DentalServiceRequest request) {
        return catalogService.updateService(id, request);
    }

    @GetMapping("/specialist-services")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA')")
    public List<SpecialistServiceAssignmentResponse> specialistServices() {
        return catalogService.findSpecialistServices();
    }

    @PostMapping("/specialist-services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignService(@Valid @RequestBody SpecialistServiceAssignmentRequest request) {
        catalogService.assignService(request.specialistId(), request.serviceId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/specialist-schedules")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA','PACIENTE')")
    public List<SpecialistScheduleResponse> schedules() {
        return catalogService.findSchedules();
    }

    @PostMapping("/specialist-schedules")
    @PreAuthorize("hasRole('ADMIN')")
    public SpecialistScheduleResponse createSchedule(@Valid @RequestBody SpecialistScheduleRequest request) {
        return catalogService.createSchedule(request);
    }
}
