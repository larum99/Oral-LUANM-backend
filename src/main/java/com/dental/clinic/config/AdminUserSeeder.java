package com.dental.clinic.config;

import com.dental.clinic.modules.auth.entity.Role;
import com.dental.clinic.modules.auth.entity.User;
import com.dental.clinic.modules.auth.repository.RoleRepository;
import com.dental.clinic.modules.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminUserSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserSeeder.class);
    private static final String ACTIVE_STATUS = "ACTIVO";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminUserSeeder(
            UserRepository userRepository,
            RoleRepository roleRepository,
            BCryptPasswordEncoder passwordEncoder,
            @Value("${APP_ADMIN_EMAIL:admin@admin.com}") String adminEmail,
            @Value("${APP_ADMIN_PASSWORD:12345678}") String adminPassword) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> createRole("ADMIN", "Administracion completa de la plataforma"));

        User admin = userRepository.findByEmailIgnoreCase(adminEmail)
                .orElseGet(User::new);
        boolean newAdmin = admin.getId() == null;

        admin.setRole(adminRole);
        admin.setFirstname("Admin");
        admin.setLastName("Local");
        admin.setEmail(adminEmail);
        admin.setStatus(ACTIVE_STATUS);

        if (newAdmin) {
            validateAdminPassword();
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        } else {
            log.info("Admin local existente detectado; no se modifica su contrasena.");
        }

        userRepository.save(admin);
    }

    private void validateAdminPassword() {
        if (adminPassword == null || adminPassword.length() < 8) {
            throw new IllegalStateException("APP_ADMIN_PASSWORD debe tener al menos 8 caracteres.");
        }
        if ("12345678".equals(adminPassword)) {
            log.warn("Usando APP_ADMIN_PASSWORD por defecto. Cambialo antes de un ambiente compartido o productivo.");
        }
    }

    private Role createRole(String name, String description) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        return roleRepository.save(role);
    }
}
