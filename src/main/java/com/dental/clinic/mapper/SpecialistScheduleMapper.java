package com.dental.clinic.mapper;

import com.dental.clinic.dto.request.SpecialistScheduleRequest;
import com.dental.clinic.dto.response.SpecialistScheduleResponse;
import com.dental.clinic.entity.Specialist;
import com.dental.clinic.entity.SpecialistSchedule;
import org.springframework.stereotype.Component;

@Component
public class SpecialistScheduleMapper {

    public SpecialistSchedule toEntity(
            SpecialistScheduleRequest request,
            Specialist specialist) {

        SpecialistSchedule schedule = new SpecialistSchedule();

        schedule.setSpecialist(specialist);
        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
        schedule.setActive(request.active());

        return schedule;
    }

    public SpecialistScheduleResponse toResponse(
            SpecialistSchedule schedule) {

        Long specialistId = schedule.getSpecialist() != null
                ? schedule.getSpecialist().getId()
                : null;

        return new SpecialistScheduleResponse(
                schedule.getId(),
                specialistId,
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getActive()
        );
    }
}