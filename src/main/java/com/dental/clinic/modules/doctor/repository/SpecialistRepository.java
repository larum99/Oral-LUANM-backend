package com.dental.clinic.modules.doctor.repository;

import com.dental.clinic.modules.doctor.entity.Specialist;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SpecialistRepository extends JpaRepository<Specialist, Long> {
    boolean existsByUserId(Long userId);
    boolean existsByProfessionalLicense(String professionalLicense);
    Optional<Specialist> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "services"})
    Optional<Specialist> findWithUserAndServicesById(Long id);

    @EntityGraph(attributePaths = {"user", "services"})
    @Query("SELECT s FROM Specialist s ORDER BY s.id")
    List<Specialist> findAllDetailed();
}
