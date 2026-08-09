package com.dental.clinic.modules.auth.mapper;

import com.dental.clinic.modules.auth.dto.response.PermissionResponse;
import com.dental.clinic.modules.auth.dto.response.RolePermissionResponse;
import com.dental.clinic.modules.auth.dto.response.RoleResponse;
import com.dental.clinic.modules.auth.dto.response.UserAdminResponse;
import com.dental.clinic.modules.patient.entity.Patient;
import com.dental.clinic.modules.auth.entity.Permission;
import com.dental.clinic.modules.auth.entity.Role;
import com.dental.clinic.modules.auth.entity.User;

import java.util.Map;
import java.util.Optional;

public final class AccessControlMapper {

    private AccessControlMapper() {
    }

    public static UserAdminResponse toUserResponse(User user, Map<Long, Patient> patientsByUserId) {
        Patient patient = patientsByUserId.get(user.getId());
        String firstName = Optional.ofNullable(user.getFirstname()).orElse("");
        String lastName = Optional.ofNullable(user.getLastName()).orElse("");
        String name = (firstName + " " + lastName).trim();
        return new UserAdminResponse(
                user.getId(),
                firstName,
                lastName,
                name,
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                user.getRole().getId(),
                user.getRole().getName(),
                patient == null ? null : patient.getId()
        );
    }

    public static RoleResponse toRoleResponse(Role role) {
        return new RoleResponse(role.getId(), role.getName(), role.getDescription());
    }

    public static PermissionResponse toPermissionResponse(Permission permission) {
        return new PermissionResponse(permission.getId(), permission.getCode(), permission.getDescription());
    }

    public static RolePermissionResponse toRolePermissionResponse(Role role, Permission permission) {
        return new RolePermissionResponse(role.getId(), role.getName(), permission.getId(), permission.getCode());
    }
}
