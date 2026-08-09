package com.dental.clinic.modules.auth.controller;

import com.dental.clinic.modules.auth.dto.request.RolePermissionsRequest;
import com.dental.clinic.modules.auth.dto.request.UserAdminRequest;
import com.dental.clinic.modules.auth.dto.response.PermissionResponse;
import com.dental.clinic.modules.auth.dto.response.RolePermissionResponse;
import com.dental.clinic.modules.auth.dto.response.RoleResponse;
import com.dental.clinic.modules.auth.dto.response.UserAdminResponse;
import com.dental.clinic.modules.auth.service.AccessControlService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UsersRolesController {

    private final AccessControlService accessControlService;

    public UsersRolesController(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserAdminResponse> users() {
        return accessControlService.listUsers();
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public UserAdminResponse createUser(@Valid @RequestBody UserAdminRequest request) {
        return accessControlService.createUser(request);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserAdminResponse updateUser(@PathVariable Long id, @Valid @RequestBody UserAdminRequest request) {
        return accessControlService.updateUser(id, request);
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> roles() {
        return accessControlService.listRoles();
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionResponse> permissions() {
        return accessControlService.listPermissions();
    }

    @GetMapping("/role-permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RolePermissionResponse> rolePermissions() {
        return accessControlService.listRolePermissions();
    }

    @PutMapping("/roles/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateRolePermissions(@PathVariable Short roleId,
                                                      @Valid @RequestBody RolePermissionsRequest request) {
        accessControlService.updateRolePermissions(roleId, request);
        return ResponseEntity.noContent().build();
    }
}
