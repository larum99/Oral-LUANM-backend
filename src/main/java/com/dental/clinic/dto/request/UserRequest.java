package com.dental.clinic.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotNull
        Short roleId,
        @NotBlank
        @Size(max = 100)
        String firstName,
        @Size(max = 100)
        String lastName,
        @NotBlank
        @Email
        @Size(max = 150)
        String email,
        @Size(max = 20)
        String phone,
        @NotBlank
        @Size(min = 6, max = 255)
        String password,
        @NotBlank
        String status

) {
}