package com.dental.clinic.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        Long roleId,
        String roleName,
        String firstName,
        String lastName,
        String email,
        String phone,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastLogin

) {
}