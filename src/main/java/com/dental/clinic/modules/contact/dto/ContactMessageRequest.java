package com.dental.clinic.modules.contact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactMessageRequest(
        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres.")
        String fullName,

        @NotBlank(message = "El telefono es obligatorio.")
        @Pattern(regexp = "\\d{10}", message = "El telefono debe tener 10 numeros.")
        String phone,

        @NotBlank(message = "El correo es obligatorio.")
        @Email(message = "El correo no es valido.")
        @Size(max = 150, message = "El correo no puede superar 150 caracteres.")
        String email,

        @NotBlank(message = "El servicio de interes es obligatorio.")
        @Size(max = 120, message = "El servicio de interes no puede superar 120 caracteres.")
        String service,

        @NotBlank(message = "El mensaje es obligatorio.")
        @Size(max = 1000, message = "El mensaje no puede superar 1000 caracteres.")
        String message,

        @Size(max = 80, message = "El origen no puede superar 80 caracteres.")
        String source
) {
}
