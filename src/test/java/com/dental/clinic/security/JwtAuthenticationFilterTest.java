package com.dental.clinic.modules.auth.security;

import com.dental.clinic.modules.auth.entity.Role;
import com.dental.clinic.modules.auth.entity.User;
import com.dental.clinic.modules.auth.repository.RoleRepository;
import com.dental.clinic.modules.auth.repository.UserRepository;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JwtAuthenticationFilterTest {

    @Autowired
    private JwtAuthenticationFilter filter;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void usesCurrentDatabaseRoleInsteadOfRoleStoredInToken() throws ServletException, IOException {
        Role admin = role("ADMIN");
        Role patient = role("PACIENTE");
        User user = user("jwt-role-%s@test.com".formatted(System.nanoTime()), admin);
        String token = jwtService.generateToken(user, "admin");
        user.setRole(patient);
        userRepository.saveAndFlush(user);
        SecurityContextHolder.clearContext();

        doFilter(token);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("ROLE_PACIENTE", SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void rejectsTokenWhenUserIdDoesNotMatchCurrentUser() throws ServletException, IOException {
        Role admin = role("ADMIN");
        String email = "jwt-recycled-%s@test.com".formatted(System.nanoTime());
        User tokenOwner = user(email, admin);
        String token = jwtService.generateToken(tokenOwner, "admin");
        userRepository.delete(tokenOwner);
        userRepository.flush();
        user(email, admin);
        SecurityContextHolder.clearContext();

        doFilter(token);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private void doFilter(String token) throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
    }

    private User user(String email, Role role) {
        User user = new User();
        user.setRole(role);
        user.setFirstname("Jwt");
        user.setLastName("User");
        user.setEmail(email);
        user.setPhone("3001234567");
        user.setPasswordHash("hash");
        user.setStatus("ACTIVO");
        return userRepository.save(user);
    }

    private Role role(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setDescription(name);
            return roleRepository.save(role);
        });
    }
}