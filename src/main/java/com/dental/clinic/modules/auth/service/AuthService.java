package com.dental.clinic.modules.auth.service;

import com.dental.clinic.modules.auth.dto.request.LoginRequest;
import com.dental.clinic.modules.auth.dto.response.AuthUserResponse;

public interface AuthService {
    AuthUserResponse login(LoginRequest request);
}
