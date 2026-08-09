package com.dental.clinic.modules.auth.controller;

import com.dental.clinic.modules.auth.dto.request.PatientRegistrationRequest;
import com.dental.clinic.modules.auth.dto.response.RegistrationResponse;
import com.dental.clinic.modules.auth.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/register")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse registerPatient(@Valid @RequestBody PatientRegistrationRequest request) {
        return registrationService.registerPatient(request);
    }
}
