package com.dental.clinic.service;

import com.dental.clinic.dto.request.DentalServiceRequest;
import com.dental.clinic.dto.response.DentalServiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DentalServiceService {
    DentalServiceResponse createDentalService(DentalServiceRequest request);
    DentalServiceResponse findDentalServiceById(Long id);
    Page<DentalServiceResponse> findAllDentalServices(Pageable pageable);
    DentalServiceResponse updateDentalService(Long id, DentalServiceRequest request);
    void deleteDentalService(Long id);
}
