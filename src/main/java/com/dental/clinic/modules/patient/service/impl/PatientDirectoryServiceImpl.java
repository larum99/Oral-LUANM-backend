package com.dental.clinic.modules.patient.service.impl;

import com.dental.clinic.modules.patient.dto.response.PatientDirectoryResponse;
import com.dental.clinic.modules.patient.mapper.PatientDirectoryMapper;
import com.dental.clinic.modules.patient.repository.PatientRepository;
import com.dental.clinic.modules.patient.service.PatientDirectoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PatientDirectoryServiceImpl implements PatientDirectoryService {
    private final PatientRepository patientRepository;
    private final PatientDirectoryMapper patientDirectoryMapper;

    public PatientDirectoryServiceImpl(PatientRepository patientRepository, PatientDirectoryMapper patientDirectoryMapper) {
        this.patientRepository = patientRepository;
        this.patientDirectoryMapper = patientDirectoryMapper;
    }

    @Override
    public List<PatientDirectoryResponse> findAll() {
        return patientRepository.findAllDetailed().stream().map(patientDirectoryMapper::toResponse).toList();
    }
}
