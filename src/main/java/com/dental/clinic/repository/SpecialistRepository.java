package com.dental.clinic.repository;

import com.dental.clinic.entity.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialistRepository extends JpaRepository<Specialist, Long> {
    boolean existsByUserId(Long userId);
    Optional<Specialist> findByUserId(Long userId);
    boolean existsByProfessionalLicense(String professionalLicense);

}