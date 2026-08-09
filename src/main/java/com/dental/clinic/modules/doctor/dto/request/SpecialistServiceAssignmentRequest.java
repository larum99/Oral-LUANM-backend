package com.dental.clinic.modules.doctor.dto.request;

import jakarta.validation.constraints.NotNull;

public record SpecialistServiceAssignmentRequest(@NotNull Long specialistId, @NotNull Long serviceId) {
}
