package com.dental.clinic.repository;

import com.dental.clinic.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Short> {

    Optional<Permission> findByCode(String code);

}