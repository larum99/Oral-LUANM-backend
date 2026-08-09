package com.dental.clinic.modules.appointment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AppointmentStatusRequest(
        @NotBlank
        String status,
        @Size(max = 255)
        String comment
) {
}
