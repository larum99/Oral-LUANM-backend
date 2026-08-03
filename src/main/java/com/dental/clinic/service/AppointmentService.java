package com.dental.clinic.service;

import com.dental.clinic.dto.request.AppointmentRequest;
import com.dental.clinic.dto.response.AppointmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);
    AppointmentResponse findAppointmentById(Long id);
    Page<AppointmentResponse> findAllAppointments(Pageable pageable);
    AppointmentResponse updateAppointment(Long id, AppointmentRequest request);
    void deleteAppointment(Long id);
}