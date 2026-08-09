package com.dental.clinic.modules.doctor.repository;

import com.dental.clinic.modules.doctor.entity.DentalService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DentalServiceRepository extends JpaRepository<DentalService, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<DentalService> findByNameIgnoreCase(String name);
}
