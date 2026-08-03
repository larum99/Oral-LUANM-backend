package com.dental.clinic.dto.response;

import java.time.LocalTime;

public record SpecialistScheduleResponse(

        Long id,
        Long specialistId,
        Short dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        Boolean active
) {
}