package com.dental.clinic.modules.appointment.controller;

import com.dental.clinic.modules.appointment.dto.request.AppointmentRequest;
import com.dental.clinic.modules.appointment.dto.request.AppointmentStatusRequest;
import com.dental.clinic.modules.patient.dto.request.ClinicalEvolutionRequest;
import com.dental.clinic.modules.patient.dto.request.MedicalHistoryRequest;
import com.dental.clinic.modules.appointment.dto.response.AppointmentHistoryResponse;
import com.dental.clinic.modules.appointment.dto.response.AppointmentResponse;
import com.dental.clinic.modules.patient.dto.response.ClinicalEvolutionResponse;
import com.dental.clinic.modules.patient.dto.response.MedicalHistoryResponse;
import com.dental.clinic.modules.appointment.service.AppointmentService;
import com.dental.clinic.modules.patient.service.ClinicalRecordService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AppointmentsClinicalController {

    private final AppointmentService appointmentService;
    private final ClinicalRecordService clinicalRecordService;

    public AppointmentsClinicalController(AppointmentService appointmentService,
                                          ClinicalRecordService clinicalRecordService) {
        this.appointmentService = appointmentService;
        this.clinicalRecordService = clinicalRecordService;
    }

    @GetMapping("/appointments")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA')")
    public List<AppointmentResponse> appointments() {
        return appointmentService.findAll();
    }

    @GetMapping("/my-appointments")
    @PreAuthorize("isAuthenticated()")
    public List<AppointmentResponse> myAppointments(Authentication authentication) {
        return appointmentService.findMine(authentication.getName());
    }

    @PostMapping("/appointments")
    @PreAuthorize("isAuthenticated()")
    public AppointmentResponse createAppointment(@Valid @RequestBody AppointmentRequest request, Authentication authentication) {
        return appointmentService.createMine(request, authentication.getName());
    }

    @PatchMapping("/appointments/{id}")
    @PreAuthorize("isAuthenticated()")
    public AppointmentResponse rescheduleAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentRequest request, Authentication authentication) {
        return appointmentService.update(id, request, authentication.getName());
    }

    @PatchMapping("/appointments/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA')")
    public AppointmentResponse updateAppointmentStatus(@PathVariable Long id, @Valid @RequestBody AppointmentStatusRequest request, Authentication authentication) {
        return appointmentService.updateStatus(id, request, authentication.getName());
    }

    @GetMapping("/appointment-history")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA')")
    public List<AppointmentHistoryResponse> appointmentHistory() {
        return clinicalRecordService.listAppointmentHistory();
    }

    @GetMapping("/medical-histories")
    @PreAuthorize("hasAnyRole('ADMIN','ESPECIALISTA')")
    public List<MedicalHistoryResponse> medicalHistories() {
        return clinicalRecordService.listMedicalHistories();
    }

    @PostMapping("/medical-histories")
    @PreAuthorize("hasAnyRole('ADMIN','ESPECIALISTA')")
    public MedicalHistoryResponse upsertMedicalHistory(@Valid @RequestBody MedicalHistoryRequest request) {
        return clinicalRecordService.upsertMedicalHistory(request);
    }

    @GetMapping("/clinical-evolutions")
    @PreAuthorize("hasAnyRole('ADMIN','ESPECIALISTA')")
    public List<ClinicalEvolutionResponse> clinicalEvolutions() {
        return clinicalRecordService.listClinicalEvolutions();
    }

    @PostMapping("/clinical-evolutions")
    @PreAuthorize("hasAnyRole('ADMIN','ESPECIALISTA')")
    public ClinicalEvolutionResponse createClinicalEvolution(@Valid @RequestBody ClinicalEvolutionRequest request) {
        return clinicalRecordService.createClinicalEvolution(request);
    }
}