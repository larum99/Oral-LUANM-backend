package com.dental.clinic.modules.doctor.repository;

import com.dental.clinic.modules.doctor.entity.SpecialistSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialistScheduleRepository extends JpaRepository<SpecialistSchedule, Long> {
    List<SpecialistSchedule> findBySpecialistIdAndDayOfWeek(Long specialistId, Short dayOfWeek);
    List<SpecialistSchedule> findBySpecialistIdAndDayOfWeekAndActiveTrue(Long specialistId, Short dayOfWeek);
}
