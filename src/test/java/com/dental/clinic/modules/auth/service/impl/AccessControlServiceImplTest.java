package com.dental.clinic.modules.auth.service.impl;

import com.dental.clinic.modules.auth.dto.request.RolePermissionsRequest;
import com.dental.clinic.modules.auth.dto.request.UserAdminRequest;
import com.dental.clinic.modules.auth.dto.response.RolePermissionResponse;
import com.dental.clinic.modules.auth.dto.response.UserAdminResponse;
import com.dental.clinic.modules.auth.entity.Permission;
import com.dental.clinic.modules.auth.entity.Role;
import com.dental.clinic.modules.patient.repository.PatientRepository;
import com.dental.clinic.modules.auth.repository.PermissionRepository;
import com.dental.clinic.modules.auth.repository.RoleRepository;
import com.dental.clinic.modules.auth.service.AccessControlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AccessControlServiceImplTest {

    @Autowired
    private AccessControlService accessControlService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private PatientRepository patientRepository;

    @Test
    void createsPatientRecordWhenAdminCreatesPatientUser() {
        role("PACIENTE");
        UserAdminRequest request = userRequest("patient-admin-%s@test.com".formatted(System.nanoTime()), "Paciente");

        UserAdminResponse response = accessControlService.createUser(request);

        assertNotNull(response.id());
        assertNotNull(response.patientId());
        assertEquals("PACIENTE", response.role());
        assertTrue(patientRepository.existsByUserId(response.id()));
        assertThrows(IllegalArgumentException.class, () -> accessControlService.createUser(request));
    }

    @Test
    void updatesRolePermissionsAndRejectsUnknownPermission() {
        Role admin = role("ADMIN");
        Permission appointments = permission("appointments", "Gestionar citas");
        permission("users", "Gestionar usuarios");

        accessControlService.updateRolePermissions(admin.getId(), new RolePermissionsRequest(List.of(appointments.getCode())));

        List<RolePermissionResponse> rolePermissions = accessControlService.listRolePermissions();
        assertTrue(rolePermissions.stream().anyMatch(item -> item.role().equals("ADMIN") && item.permission().equals("appointments")));
        assertThrows(IllegalArgumentException.class,
                () -> accessControlService.updateRolePermissions(admin.getId(), new RolePermissionsRequest(List.of("missing"))));
    }

    private UserAdminRequest userRequest(String email, String role) {
        return new UserAdminRequest(
                "Paciente Test",
                null,
                null,
                email,
                "3001234567",
                role,
                null,
                "Activo",
                "12345678",
                "DOC-%s".formatted(System.nanoTime()),
                null
        );
    }

    private Role role(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setDescription(name);
            return roleRepository.save(role);
        });
    }

    private Permission permission(String code, String description) {
        return permissionRepository.findByCode(code).orElseGet(() -> {
            Permission permission = new Permission();
            permission.setCode(code);
            permission.setDescription(description);
            return permissionRepository.save(permission);
        });
    }
}