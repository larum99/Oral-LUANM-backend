package com.dental.clinic.modules.auth.dto.response;

public record PermissionResponse(
        Short id,
        String code,
        String description
) {
}
