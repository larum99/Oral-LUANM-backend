package com.dental.clinic.repository;

import com.dental.clinic.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByUserId(Long userId);
    Optional<Patient> findByUserId(Long userId);
}