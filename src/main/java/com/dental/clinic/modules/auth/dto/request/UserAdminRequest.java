package com.dental.clinic.modules.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserAdminRequest(
        @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
        String name,
        @Size(max = 100, message = "El primer nombre no puede superar 100 caracteres")
        String firstName,
        @Size(max = 100, message = "El apellido no puede superar 100 caracteres")
        String lastName,
        @Email(message = "El correo no es valido")
        @Size(max = 150, message = "El correo no puede superar 150 caracteres")
        String email,
        @Size(max = 20, message = "El telefono no puede superar 20 caracteres")
        String phone,
        String role,
        Short roleId,
        String status,
        String password,
        String documentNumber,
        String patientDocument
) {
}
