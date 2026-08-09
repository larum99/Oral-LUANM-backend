package com.dental.clinic.modules.doctor.service;

import com.dental.clinic.modules.doctor.dto.request.DentalServiceRequest;
import com.dental.clinic.modules.doctor.dto.request.SpecialistRequest;
import com.dental.clinic.modules.doctor.dto.request.SpecialistScheduleRequest;
import com.dental.clinic.modules.doctor.dto.response.DentalServiceResponse;
import com.dental.clinic.modules.doctor.dto.response.SpecialistResponse;
import com.dental.clinic.modules.doctor.dto.response.SpecialistScheduleResponse;
import com.dental.clinic.modules.doctor.dto.response.SpecialistServiceAssignmentResponse;

import java.util.List;

public interface CatalogService {
    List<SpecialistResponse> findSpecialists();
    SpecialistResponse createSpecialist(SpecialistRequest request);
    SpecialistResponse updateSpecialist(Long id, SpecialistRequest request);
    List<DentalServiceResponse> findServices();
    DentalServiceResponse createService(DentalServiceRequest request);
    DentalServiceResponse updateService(Long id, DentalServiceRequest request);
    List<SpecialistServiceAssignmentResponse> findSpecialistServices();
    void assignService(Long specialistId, Long serviceId);
    List<SpecialistScheduleResponse> findSchedules();
    SpecialistScheduleResponse createSchedule(SpecialistScheduleRequest request);
}
