package com.dental.clinic.controller;

import com.dental.clinic.dto.request.DentalServiceRequest;
import com.dental.clinic.dto.response.DentalServiceResponse;
import com.dental.clinic.service.DentalServiceService;
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
@RequestMapping(Endpoints.DENTAL_SERVICE_PATH)
public class DentalServiceController {

    private final DentalServiceService dentalServiceService;

    public DentalServiceController(DentalServiceService dentalServiceService) {
        this.dentalServiceService = dentalServiceService;
    }

    @PostMapping
    public ResponseEntity<DentalServiceResponse> createDentalService(
            @Valid @RequestBody DentalServiceRequest request) {
        DentalServiceResponse response =
                dentalServiceService.createDentalService(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<DentalServiceResponse>> findAllDentalServices(
            @PageableDefault(
                    size = PaginationConstants.DEFAULT_PAGE_SIZE,
                    sort = PaginationConstants.DEFAULT_SORT_BY
            )
            Pageable pageable) {
        Page<DentalServiceResponse> response =
                dentalServiceService.findAllDentalServices(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DentalServiceResponse> findDentalServiceById(
            @PathVariable Long id) {
        DentalServiceResponse response =
                dentalServiceService.findDentalServiceById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DentalServiceResponse> updateDentalService(
            @PathVariable Long id,
            @Valid @RequestBody DentalServiceRequest request) {
        DentalServiceResponse response =
                dentalServiceService.updateDentalService(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDentalService(
            @PathVariable Long id) {
        dentalServiceService.deleteDentalService(id);
        return ResponseEntity.noContent().build();
    }
}