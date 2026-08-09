package com.dental.clinic.modules.auth.repository;

import com.dental.clinic.modules.auth.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByTokenHashAndUsedAtIsNullAndExpiresAtAfter(String tokenHash, LocalDateTime now);
}
