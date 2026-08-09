package com.dental.clinic.modules.patient.repository;

import com.dental.clinic.modules.patient.entity.ClinicalEvolution;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClinicalEvolutionRepository extends JpaRepository<ClinicalEvolution, Long> {

    @EntityGraph(attributePaths = {"medicalHistory", "appointment", "specialist"})
    @Query("SELECT e FROM ClinicalEvolution e ORDER BY e.registeredAt DESC, e.id DESC")
    List<ClinicalEvolution> findAllDetailed();

    @EntityGraph(attributePaths = {"medicalHistory", "appointment", "specialist"})
    Optional<ClinicalEvolution> findDetailedById(Long id);
}