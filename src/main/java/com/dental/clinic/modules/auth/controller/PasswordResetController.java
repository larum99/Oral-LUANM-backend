package com.dental.clinic.modules.auth.controller;

import com.dental.clinic.modules.auth.dto.request.PasswordResetConfirmRequest;
import com.dental.clinic.modules.auth.dto.request.PasswordResetRequest;
import com.dental.clinic.shared.response.MessageResponse;
import com.dental.clinic.modules.auth.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/password-reset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/request")
    public MessageResponse requestReset(@Valid @RequestBody PasswordResetRequest request) {
        return passwordResetService.requestReset(request);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
        passwordResetService.confirmReset(request);
        return ResponseEntity.noContent().build();
    }
}
