package com.dental.clinic.repository;

import com.dental.clinic.entity.Appointment;
import com.dental.clinic.utils.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
            SELECT COUNT(a) > 0
            FROM Appointment a
            WHERE a.specialist.id = :specialistId
              AND a.startDatetime < :endDatetime
              AND a.endDatetime > :startDatetime
              AND a.status <> :cancelledStatus
            """)
    boolean existsOverlappingAppointmentForSpecialist(
            Long specialistId,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime,
            AppointmentStatus cancelledStatus
    );

    @Query("""
            SELECT COUNT(a) > 0
            FROM Appointment a
            WHERE a.patient.id = :patientId
              AND a.startDatetime < :endDatetime
              AND a.endDatetime > :startDatetime
              AND a.status <> :cancelledStatus
            """)
    boolean existsOverlappingAppointmentForPatient(
            Long patientId,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime,
            AppointmentStatus cancelledStatus
    );
}