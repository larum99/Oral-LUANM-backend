package com.dental.clinic.controller;

import com.dental.clinic.dto.request.SpecialistRequest;
import com.dental.clinic.dto.response.SpecialistResponse;
import com.dental.clinic.service.SpecialistService;
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
@RequestMapping(Endpoints.SPECIALISTS_PATH)
public class SpecialistController {

    private final SpecialistService specialistService;

    public SpecialistController(SpecialistService specialistService) {
        this.specialistService = specialistService;
    }

    @PostMapping
    public ResponseEntity<SpecialistResponse> createSpecialist(
            @Valid @RequestBody SpecialistRequest request) {
        SpecialistResponse response =
                specialistService.createSpecialist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<SpecialistResponse>> findAllSpecialists(
            @PageableDefault(
                    size = PaginationConstants.DEFAULT_PAGE_SIZE,
                    sort = PaginationConstants.DEFAULT_SORT_BY)
            Pageable pageable) {
        Page<SpecialistResponse> response =
                specialistService.findAllSpecialists(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialistResponse> findSpecialistById(
            @PathVariable Long id) {
        SpecialistResponse response =
                specialistService.findSpecialistById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialistResponse> updateSpecialist(
            @PathVariable Long id,
            @Valid @RequestBody SpecialistRequest request) {
        SpecialistResponse response =
                specialistService.updateSpecialist(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialist(
            @PathVariable Long id) {
        specialistService.deleteSpecialist(id);
        return ResponseEntity.noContent().build();
    }
}