package com.dental.clinic.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetConfirmRequest(@NotBlank String token, @NotBlank @Size(min = 8) String password) {
}
