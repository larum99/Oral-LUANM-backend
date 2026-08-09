package com.dental.clinic.modules.patient.repository;

import com.dental.clinic.modules.patient.entity.Patient;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByUserId(Long userId);
    Optional<Patient> findByUserId(Long userId);
    Optional<Patient> findByDocumentNumber(String documentNumber);

    @EntityGraph(attributePaths = "user")
    @Query("SELECT p FROM Patient p ORDER BY p.id")
    List<Patient> findAllDetailed();
}
