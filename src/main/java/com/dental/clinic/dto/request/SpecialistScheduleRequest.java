package com.dental.clinic.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalTime;

public record SpecialistScheduleRequest(

        @NotNull
        Long specialistId,
        @NotNull
        @Min(1)
        @Max(7)
        Short dayOfWeek,
        @NotNull
        LocalTime startTime,
        @NotNull
        LocalTime endTime,
        @NotNull
        Boolean active
) {
}