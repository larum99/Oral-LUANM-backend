package com.dental.clinic.service;

import com.dental.clinic.dto.request.SpecialistRequest;
import com.dental.clinic.dto.response.SpecialistResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpecialistService {
    SpecialistResponse createSpecialist(SpecialistRequest request);
    SpecialistResponse findSpecialistById(Long id);
    Page<SpecialistResponse> findAllSpecialists(Pageable pageable);
    SpecialistResponse updateSpecialist(Long id, SpecialistRequest request);
    void deleteSpecialist(Long id);
}