package com.dental.clinic.modules.auth.repository;

import com.dental.clinic.modules.auth.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "role")
    Optional<User> findWithRoleByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "role")
    @Query("SELECT u FROM User u ORDER BY u.id")
    List<User> findAllDetailed();

    @EntityGraph(attributePaths = "role")
    Optional<User> findWithRoleById(Long id);
}