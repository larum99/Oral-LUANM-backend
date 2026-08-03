package com.dental.clinic.service;

import com.dental.clinic.dto.request.SpecialistScheduleRequest;
import com.dental.clinic.dto.response.SpecialistScheduleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpecialistScheduleService {
    SpecialistScheduleResponse createSpecialistSchedule(SpecialistScheduleRequest request);
    SpecialistScheduleResponse findSpecialistScheduleById(Long id);
    Page<SpecialistScheduleResponse> findAllSpecialistSchedules(Pageable pageable);
    SpecialistScheduleResponse updateSpecialistSchedule(Long id, SpecialistScheduleRequest request);
    void deleteSpecialistSchedule(Long id);
}