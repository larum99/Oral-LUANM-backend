package com.dental.clinic.dto.response;

public record AuthUserResponse(
        Long id,
        String email,
        String name,
        String role,
        String path,
        String phone,
        String token,
        String tokenType,
        Long expiresInSeconds
) {
}
