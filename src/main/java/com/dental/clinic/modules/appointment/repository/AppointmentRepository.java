package com.dental.clinic.modules.appointment.repository;

import com.dental.clinic.modules.appointment.entity.Appointment;
import com.dental.clinic.modules.appointment.util.AppointmentStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @EntityGraph(attributePaths = {"patient.user", "specialist.user", "service", "createdBy"})
    @Query("SELECT a FROM Appointment a ORDER BY a.startDatetime DESC")
    List<Appointment> findAllDetailed();

    @EntityGraph(attributePaths = {"patient.user", "specialist.user", "service", "createdBy"})
    @Query("SELECT a FROM Appointment a WHERE a.patient.user.id = :userId ORDER BY a.startDatetime DESC")
    List<Appointment> findDetailedByPatientUserId(Long userId);

    @EntityGraph(attributePaths = {"patient.user", "specialist.user", "service", "createdBy"})
    Optional<Appointment> findDetailedById(Long id);

    @Query("""
            SELECT COUNT(a) > 0
            FROM Appointment a
            WHERE a.specialist.id = :specialistId
              AND a.startDatetime < :endDatetime
              AND a.endDatetime > :startDatetime
              AND a.status <> :cancelledStatus
              AND (:excludeId IS NULL OR a.id <> :excludeId)
            """)
    boolean existsOverlappingForSpecialist(Long specialistId, LocalDateTime startDatetime, LocalDateTime endDatetime, AppointmentStatus cancelledStatus, Long excludeId);

    @Query("""
            SELECT COUNT(a) > 0
            FROM Appointment a
            WHERE a.patient.id = :patientId
              AND a.startDatetime < :endDatetime
              AND a.endDatetime > :startDatetime
              AND a.status <> :cancelledStatus
              AND (:excludeId IS NULL OR a.id <> :excludeId)
            """)
    boolean existsOverlappingForPatient(Long patientId, LocalDateTime startDatetime, LocalDateTime endDatetime, AppointmentStatus cancelledStatus, Long excludeId);
}
