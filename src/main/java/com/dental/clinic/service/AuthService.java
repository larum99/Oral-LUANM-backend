package com.dental.clinic.service;

import com.dental.clinic.dto.request.LoginRequest;
import com.dental.clinic.dto.response.AuthUserResponse;

public interface AuthService {
    AuthUserResponse login(LoginRequest request);
}
