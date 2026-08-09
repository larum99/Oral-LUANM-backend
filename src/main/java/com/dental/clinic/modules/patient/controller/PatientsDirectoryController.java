package com.dental.clinic.modules.patient.controller;

import com.dental.clinic.modules.patient.dto.response.PatientDirectoryResponse;
import com.dental.clinic.modules.patient.service.PatientDirectoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PatientsDirectoryController {

    private final PatientDirectoryService patientDirectoryService;

    public PatientsDirectoryController(PatientDirectoryService patientDirectoryService) {
        this.patientDirectoryService = patientDirectoryService;
    }

    @GetMapping("/patients-directory")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIO','ESPECIALISTA')")
    public List<PatientDirectoryResponse> patientsDirectory() {
        return patientDirectoryService.findAll();
    }
}
