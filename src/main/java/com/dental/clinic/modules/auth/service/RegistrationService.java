package com.dental.clinic.modules.auth.service;

import com.dental.clinic.modules.auth.dto.request.PatientRegistrationRequest;
import com.dental.clinic.modules.auth.dto.response.RegistrationResponse;

public interface RegistrationService {
    RegistrationResponse registerPatient(PatientRegistrationRequest request);
}
