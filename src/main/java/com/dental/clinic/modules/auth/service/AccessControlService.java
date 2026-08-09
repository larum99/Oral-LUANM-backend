package com.dental.clinic.modules.auth.service;

import com.dental.clinic.modules.auth.dto.request.RolePermissionsRequest;
import com.dental.clinic.modules.auth.dto.request.UserAdminRequest;
import com.dental.clinic.modules.auth.dto.response.PermissionResponse;
import com.dental.clinic.modules.auth.dto.response.RolePermissionResponse;
import com.dental.clinic.modules.auth.dto.response.RoleResponse;
import com.dental.clinic.modules.auth.dto.response.UserAdminResponse;

import java.util.List;

public interface AccessControlService {
    List<UserAdminResponse> listUsers();
    UserAdminResponse createUser(UserAdminRequest request);
    UserAdminResponse updateUser(Long id, UserAdminRequest request);
    List<RoleResponse> listRoles();
    List<PermissionResponse> listPermissions();
    List<RolePermissionResponse> listRolePermissions();
    void updateRolePermissions(Short roleId, RolePermissionsRequest request);
}
