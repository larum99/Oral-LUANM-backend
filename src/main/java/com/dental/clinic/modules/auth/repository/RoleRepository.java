package com.dental.clinic.modules.auth.repository;

import com.dental.clinic.modules.auth.entity.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Short> {
    Optional<Role> findByName(String name);

    @EntityGraph(attributePaths = "permissions")
    Optional<Role> findWithPermissionsById(Short id);

    @EntityGraph(attributePaths = "permissions")
    @Query("SELECT DISTINCT r FROM Role r ORDER BY r.name")
    List<Role> findAllWithPermissions();
}
