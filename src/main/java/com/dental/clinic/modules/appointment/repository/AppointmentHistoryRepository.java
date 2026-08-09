package com.dental.clinic.modules.appointment.repository;

import com.dental.clinic.modules.appointment.entity.AppointmentHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppointmentHistoryRepository extends JpaRepository<AppointmentHistory, Long> {

    @EntityGraph(attributePaths = {"appointment", "changedBy"})
    @Query("SELECT h FROM AppointmentHistory h ORDER BY h.changedAt DESC")
    List<AppointmentHistory> findAllDetailed();
}