package com.dental.clinic.modules.auth.repository;

import com.dental.clinic.modules.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Short> {
    Optional<Permission> findByCode(String code);
    List<Permission> findByCodeIn(Collection<String> codes);
}
