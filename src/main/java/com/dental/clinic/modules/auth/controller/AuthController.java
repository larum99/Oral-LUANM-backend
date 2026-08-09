package com.dental.clinic.modules.auth.controller;

import com.dental.clinic.modules.auth.dto.request.LoginRequest;
import com.dental.clinic.modules.auth.dto.response.AuthUserResponse;
import com.dental.clinic.modules.auth.service.AuthService;
import com.dental.clinic.shared.util.Endpoints;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.AUTH_PATH)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthUserResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
