package com.dental.clinic.controller;

import com.dental.clinic.dto.request.PatientRequest;
import com.dental.clinic.dto.response.PatientResponse;
import com.dental.clinic.service.PatientService;
import com.dental.clinic.utils.Endpoints;
import com.dental.clinic.utils.PaginationConstants;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Endpoints.PATIENTS_PATH)
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody PatientRequest request) {

        PatientResponse response = patientService.createPatient(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<PatientResponse>> findAllPatients(
            @PageableDefault(size = PaginationConstants.DEFAULT_PAGE_SIZE,
                    sort = PaginationConstants.DEFAULT_SORT_BY) Pageable pageable) {
        Page<PatientResponse> response = patientService.findAllPatients(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> findPatientById(@PathVariable Long id) {
        PatientResponse response = patientService.findPatientById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request) {
        PatientResponse response = patientService.updatePatient(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
