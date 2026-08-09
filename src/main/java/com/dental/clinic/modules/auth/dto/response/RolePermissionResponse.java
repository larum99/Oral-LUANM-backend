package com.dental.clinic.modules.auth.dto.response;

public record RolePermissionResponse(
        Short roleId,
        String role,
        Short permissionId,
        String permission
) {
}
