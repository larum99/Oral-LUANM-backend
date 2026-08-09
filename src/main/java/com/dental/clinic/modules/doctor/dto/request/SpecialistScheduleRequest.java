package com.dental.clinic.modules.doctor.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record SpecialistScheduleRequest(
        @NotNull Long specialistId,
        @NotNull @Min(1) @Max(7) Short dayOfWeek,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        Boolean active
) {
}
