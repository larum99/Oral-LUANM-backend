package com.dental.clinic.modules.auth.service;

import com.dental.clinic.modules.auth.dto.request.PasswordResetConfirmRequest;
import com.dental.clinic.modules.auth.dto.request.PasswordResetRequest;
import com.dental.clinic.shared.response.MessageResponse;

public interface PasswordResetService {
    MessageResponse requestReset(PasswordResetRequest request);
    void confirmReset(PasswordResetConfirmRequest request);
}
