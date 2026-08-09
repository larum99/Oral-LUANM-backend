package com.dental.clinic.modules.appointment.service;

import com.dental.clinic.modules.appointment.dto.request.AppointmentRequest;
import com.dental.clinic.modules.appointment.dto.request.AppointmentStatusRequest;
import com.dental.clinic.modules.appointment.dto.response.AppointmentResponse;

import java.util.List;

public interface AppointmentService {
    List<AppointmentResponse> findAll();
    List<AppointmentResponse> findMine(String userEmail);
    AppointmentResponse create(AppointmentRequest request, String createdByEmail);
    AppointmentResponse createMine(AppointmentRequest request, String patientUserEmail);
    AppointmentResponse update(Long id, AppointmentRequest request, String changedByEmail);
    AppointmentResponse updateStatus(Long id, AppointmentStatusRequest request, String changedByEmail);
}
