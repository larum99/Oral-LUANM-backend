package com.dental.clinic.modules.patient.service;

import com.dental.clinic.modules.patient.dto.response.PatientDirectoryResponse;

import java.util.List;

public interface PatientDirectoryService {
    List<PatientDirectoryResponse> findAll();
}
