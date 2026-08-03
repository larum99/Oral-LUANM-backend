package com.dental.clinic.repository;

import com.dental.clinic.entity.DentalService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DentalServiceRepository extends JpaRepository<DentalService, Long> {
    boolean existsByName(String name);
    Optional<DentalService> findByName(String name);
}
