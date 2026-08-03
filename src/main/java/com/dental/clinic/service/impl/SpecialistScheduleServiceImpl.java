package com.dental.clinic.service.impl;

import com.dental.clinic.dto.request.SpecialistScheduleRequest;
import com.dental.clinic.dto.response.SpecialistScheduleResponse;
import com.dental.clinic.entity.Specialist;
import com.dental.clinic.entity.SpecialistSchedule;
import com.dental.clinic.exception.BusinessException;
import com.dental.clinic.exception.ResourceNotFoundException;
import com.dental.clinic.mapper.SpecialistScheduleMapper;
import com.dental.clinic.repository.SpecialistRepository;
import com.dental.clinic.repository.SpecialistScheduleRepository;
import com.dental.clinic.service.SpecialistScheduleService;
import com.dental.clinic.utils.MessageConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SpecialistScheduleServiceImpl implements SpecialistScheduleService {

    private final SpecialistScheduleRepository specialistScheduleRepository;
    private final SpecialistRepository specialistRepository;
    private final SpecialistScheduleMapper specialistScheduleMapper;

    public SpecialistScheduleServiceImpl(
            SpecialistScheduleRepository specialistScheduleRepository,
            SpecialistRepository specialistRepository,
            SpecialistScheduleMapper specialistScheduleMapper) {
        this.specialistScheduleRepository = specialistScheduleRepository;
        this.specialistRepository = specialistRepository;
        this.specialistScheduleMapper = specialistScheduleMapper;
    }

    @Override
    @Transactional
    public SpecialistScheduleResponse createSpecialistSchedule(
            SpecialistScheduleRequest request) {
        Specialist specialist = findSpecialistById(request.specialistId());
        validateSchedule(request, null);
        SpecialistSchedule schedule = specialistScheduleMapper.toEntity(request, specialist);
        SpecialistSchedule savedSchedule = specialistScheduleRepository.save(schedule);

        return specialistScheduleMapper.toResponse(savedSchedule);
    }

    @Override
    public SpecialistScheduleResponse findSpecialistScheduleById(Long id) {
        SpecialistSchedule schedule = findScheduleEntityById(id);
        return specialistScheduleMapper.toResponse(schedule);
    }

    @Override
    public Page<SpecialistScheduleResponse> findAllSpecialistSchedules(
            Pageable pageable) {
        return specialistScheduleRepository.findAll(pageable)
                .map(specialistScheduleMapper::toResponse);
    }

    @Override
    @Transactional
    public SpecialistScheduleResponse updateSpecialistSchedule(
            Long id,
            SpecialistScheduleRequest request) {

        SpecialistSchedule schedule = findScheduleEntityById(id);

        Specialist specialist = findSpecialistById(request.specialistId());

        validateSchedule(request, id);

        schedule.setSpecialist(specialist);
        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
        schedule.setActive(request.active());

        SpecialistSchedule updatedSchedule =
                specialistScheduleRepository.save(schedule);

        return specialistScheduleMapper.toResponse(updatedSchedule);
    }

    @Override
    @Transactional
    public void deleteSpecialistSchedule(Long id) {
        SpecialistSchedule schedule = findScheduleEntityById(id);
        specialistScheduleRepository.delete(schedule);
    }

    private SpecialistSchedule findScheduleEntityById(Long id) {
        return specialistScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.SCHEDULE_NOT_FOUND.formatted(id)
                ));
    }

    private Specialist findSpecialistById(Long id) {
        return specialistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.SPECIALIST_NOT_FOUND.formatted(id)
                ));
    }

    private void validateSchedule(
            SpecialistScheduleRequest request,
            Long scheduleId) {

        validateTimeRange(request.startTime(), request.endTime());

        validateScheduleOverlap(request, scheduleId);
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {

        if (!endTime.isAfter(startTime)) {
            throw new BusinessException(
                    MessageConstants.INVALID_SCHEDULE_TIME_RANGE
            );
        }
    }

    private void validateScheduleOverlap(
            SpecialistScheduleRequest request,
            Long scheduleId) {

        List<SpecialistSchedule> schedules =
                specialistScheduleRepository.findBySpecialistIdAndDayOfWeek(
                        request.specialistId(),
                        request.dayOfWeek());

        for (SpecialistSchedule existing : schedules) {

            if (scheduleId != null &&
                    existing.getId().equals(scheduleId)) {
                continue;
            }

            boolean overlap =
                    request.startTime().isBefore(existing.getEndTime())
                            && request.endTime().isAfter(existing.getStartTime());

            if (overlap) {
                throw new BusinessException(
                        MessageConstants.SCHEDULE_OVERLAP
                );
            }
        }
    }
}