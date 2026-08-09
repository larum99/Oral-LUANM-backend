package com.dental.clinic.modules.patient.repository;

import com.dental.clinic.modules.patient.entity.MedicalHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    Optional<MedicalHistory> findByPatientId(Long patientId);

    @EntityGraph(attributePaths = "patient")
    @Query("SELECT h FROM MedicalHistory h ORDER BY h.id DESC")
    List<MedicalHistory> findAllDetailed();

    @EntityGraph(attributePaths = "patient")
    Optional<MedicalHistory> findDetailedById(Long id);
}