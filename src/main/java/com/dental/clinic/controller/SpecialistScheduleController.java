package com.dental.clinic.controller;

import com.dental.clinic.dto.request.SpecialistScheduleRequest;
import com.dental.clinic.dto.response.SpecialistScheduleResponse;
import com.dental.clinic.service.SpecialistScheduleService;
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
@RequestMapping(Endpoints.SPECIALIST_SCHEDULES_PATH)
public class SpecialistScheduleController {
    private final SpecialistScheduleService specialistScheduleService;
    public SpecialistScheduleController(
            SpecialistScheduleService specialistScheduleService) {
        this.specialistScheduleService = specialistScheduleService;
    }

    @PostMapping
    public ResponseEntity<SpecialistScheduleResponse> createSpecialistSchedule(
            @Valid @RequestBody SpecialistScheduleRequest request) {
        SpecialistScheduleResponse response =
                specialistScheduleService.createSpecialistSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<SpecialistScheduleResponse>> findAllSpecialistSchedules(
            @PageableDefault(
                    size = PaginationConstants.DEFAULT_PAGE_SIZE,
                    sort = PaginationConstants.DEFAULT_SORT_BY)
            Pageable pageable) {
        Page<SpecialistScheduleResponse> response =
                specialistScheduleService.findAllSpecialistSchedules(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialistScheduleResponse> findSpecialistScheduleById(
            @PathVariable Long id) {
        SpecialistScheduleResponse response =
                specialistScheduleService.findSpecialistScheduleById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialistScheduleResponse> updateSpecialistSchedule(
            @PathVariable Long id,
            @Valid @RequestBody SpecialistScheduleRequest request) {
        SpecialistScheduleResponse response =
                specialistScheduleService.updateSpecialistSchedule(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialistSchedule(
            @PathVariable Long id) {
        specialistScheduleService.deleteSpecialistSchedule(id);
        return ResponseEntity.noContent().build();
    }
}