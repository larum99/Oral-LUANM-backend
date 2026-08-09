package com.dental.clinic.modules.auth.service.impl;

import com.dental.clinic.modules.auth.dto.request.RolePermissionsRequest;
import com.dental.clinic.modules.auth.dto.request.UserAdminRequest;
import com.dental.clinic.modules.auth.dto.response.PermissionResponse;
import com.dental.clinic.modules.auth.dto.response.RolePermissionResponse;
import com.dental.clinic.modules.auth.dto.response.RoleResponse;
import com.dental.clinic.modules.auth.dto.response.UserAdminResponse;
import com.dental.clinic.modules.patient.entity.Patient;
import com.dental.clinic.modules.auth.entity.Permission;
import com.dental.clinic.modules.auth.entity.Role;
import com.dental.clinic.modules.auth.entity.User;
import com.dental.clinic.modules.auth.mapper.AccessControlMapper;
import com.dental.clinic.modules.patient.repository.PatientRepository;
import com.dental.clinic.modules.auth.repository.PermissionRepository;
import com.dental.clinic.modules.auth.repository.RoleRepository;
import com.dental.clinic.modules.auth.repository.UserRepository;
import com.dental.clinic.modules.auth.service.AccessControlService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccessControlServiceImpl implements AccessControlService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PatientRepository patientRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AccessControlServiceImpl(UserRepository userRepository,
                                    RoleRepository roleRepository,
                                    PermissionRepository permissionRepository,
                                    PatientRepository patientRepository,
                                    BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAdminResponse> listUsers() {
        Map<Long, Patient> patientsByUserId = patientRepository.findAllDetailed().stream()
                .filter(patient -> patient.getUser() != null)
                .collect(Collectors.toMap(patient -> patient.getUser().getId(), Function.identity()));
        return userRepository.findAllDetailed().stream()
                .map(user -> AccessControlMapper.toUserResponse(user, patientsByUserId))
                .toList();
    }

    @Override
    public UserAdminResponse createUser(UserAdminRequest request) {
        String email = normalizedEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
        }
        String password = safeTrim(request.password());
        if (password.length() < 8) {
            throw new IllegalArgumentException("La contrasena del usuario es obligatoria y debe tener minimo 8 caracteres.");
        }

        User user = new User();
        applyUserFields(user, request, resolveRole(request));
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        User saved = userRepository.save(user);
        createPatientIfNeeded(saved, request);
        return findUserResponse(saved.getId());
    }

    @Override
    public UserAdminResponse updateUser(Long id, UserAdminRequest request) {
        User user = userRepository.findWithRoleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        String email = normalizedEmail(request.email());
        userRepository.findByEmailIgnoreCase(email)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Ya existe otro usuario con ese correo.");
                });

        applyUserFields(user, request, resolveRole(request));
        user.setEmail(email);
        String password = safeTrim(request.password());
        if (!password.isBlank()) {
            if (password.length() < 8) {
                throw new IllegalArgumentException("La contrasena debe tener minimo 8 caracteres.");
            }
            user.setPasswordHash(passwordEncoder.encode(password));
        }
        User saved = userRepository.save(user);
        createPatientIfNeeded(saved, request);
        return findUserResponse(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles() {
        return roleRepository.findAll().stream()
                .sorted(Comparator.comparing(Role::getId))
                .map(AccessControlMapper::toRoleResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> listPermissions() {
        return permissionRepository.findAll().stream()
                .sorted(Comparator.comparing(Permission::getCode))
                .map(AccessControlMapper::toPermissionResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolePermissionResponse> listRolePermissions() {
        return roleRepository.findAllWithPermissions().stream()
                .flatMap(role -> role.getPermissions().stream()
                        .sorted(Comparator.comparing(Permission::getCode))
                        .map(permission -> AccessControlMapper.toRolePermissionResponse(role, permission)))
                .toList();
    }

    @Override
    public void updateRolePermissions(Short roleId, RolePermissionsRequest request) {
        Role role = roleRepository.findWithPermissionsById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado."));
        Set<String> requestedCodes = request.permissions().stream()
                .map(this::normalizedPermissionCode)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<Permission> permissions = permissionRepository.findByCodeIn(requestedCodes);
        Set<String> foundCodes = permissions.stream().map(Permission::getCode).collect(Collectors.toSet());
        requestedCodes.stream()
                .filter(code -> !foundCodes.contains(code))
                .findFirst()
                .ifPresent(code -> {
                    throw new IllegalArgumentException("Permiso no encontrado: " + code);
                });
        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);
        roleRepository.save(role);
    }

    private UserAdminResponse findUserResponse(Long id) {
        User user = userRepository.findWithRoleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        return AccessControlMapper.toUserResponse(user, patientRepository.findByUserId(id)
                .map(patient -> Map.of(id, patient))
                .orElseGet(Map::of));
    }

    private void applyUserFields(User user, UserAdminRequest request, Role role) {
        String[] names = splitName(request.name());
        user.setRole(role);
        user.setFirstname(nonBlank(request.firstName(), names[0]));
        user.setLastName(nullable(nonBlank(request.lastName(), names[1])));
        user.setPhone(nullable(request.phone()));
        user.setStatus(dbStatus(request.status()));
    }

    private Role resolveRole(UserAdminRequest request) {
        if (request.roleId() != null) {
            return roleRepository.findById(request.roleId())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado."));
        }
        return roleRepository.findByName(dbRole(request.role()))
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado."));
    }

    private void createPatientIfNeeded(User user, UserAdminRequest request) {
        String role = user.getRole().getName().toUpperCase(Locale.ROOT);
        if (!(role.equals("PACIENTE") || role.equals("CLIENTE") || role.equals("CLIENT"))) {
            return;
        }
        if (patientRepository.existsByUserId(user.getId())) {
            return;
        }
        Patient patient = new Patient();
        patient.setUser(user);
        patient.setDocumentType("CC");
        patient.setDocumentNumber(nonBlank(nonBlank(request.documentNumber(), request.patientDocument()), "AUTO-" + user.getId()));
        patient.setAcceptsData(true);
        patient.setAcceptsPromotions(false);
        patientRepository.save(patient);
    }

    private String normalizedEmail(String email) {
        String value = safeTrim(email).toLowerCase(Locale.ROOT);
        if (value.isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio.");
        }
        return value;
    }

    private String normalizedPermissionCode(String permission) {
        String value = safeTrim(permission);
        if (value.isBlank()) {
            throw new IllegalArgumentException("Los permisos no pueden estar vacios.");
        }
        return value;
    }

    private String dbRole(String role) {
        String normalized = safeTrim(role).toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "ADMIN", "ADMINISTRADOR" -> "ADMIN";
            case "SECRETARIO", "SECRETARIA", "SECRETARY" -> "SECRETARIO";
            case "ESPECIALISTA", "SPECIALIST" -> "ESPECIALISTA";
            case "AUXILIAR" -> "AUXILIAR";
            default -> "PACIENTE";
        };
    }

    private String dbStatus(String status) {
        String normalized = safeTrim(status).toUpperCase(Locale.ROOT);
        if (normalized.startsWith("INACT")) return "INACTIVO";
        if (normalized.startsWith("BLOQ")) return "BLOQUEADO";
        return "ACTIVO";
    }

    private String[] splitName(String name) {
        String clean = safeTrim(name);
        if (clean.isBlank()) {
            return new String[]{"Usuario", ""};
        }
        String[] parts = clean.split("\\s+", 2);
        return new String[]{parts[0], parts.length > 1 ? parts[1] : ""};
    }

    private String nonBlank(String value, String fallback) {
        String clean = safeTrim(value);
        return clean.isBlank() ? fallback : clean;
    }

    private String nullable(String value) {
        String clean = safeTrim(value);
        return clean.isBlank() ? null : clean;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
