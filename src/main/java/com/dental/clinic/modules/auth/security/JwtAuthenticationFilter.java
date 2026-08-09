package com.dental.clinic.modules.auth.security;

import com.dental.clinic.modules.auth.entity.User;
import com.dental.clinic.modules.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ACTIVE_STATUS = "ACTIVO";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith(BEARER_PREFIX) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = authorization.substring(BEARER_PREFIX.length()).trim();
            jwtService.validate(token).ifPresent(claims -> userRepository.findWithRoleByEmailIgnoreCase(claims.email()).ifPresent(user -> authenticateIfCurrent(claims, user)));
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateIfCurrent(JwtService.JwtClaims claims, User user) {
        if (!ACTIVE_STATUS.equals(user.getStatus()) || !user.getId().equals(claims.userId())) {
            return;
        }
        String roleAuthority = "ROLE_" + roleName(user.getRole().getName());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority(roleAuthority))
        );
        authentication.setDetails(user.getId());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String roleName(String role) {
        return switch (String.valueOf(role).trim().toUpperCase(Locale.ROOT)) {
            case "ADMIN", "ADMINISTRADOR" -> "ADMIN";
            case "SECRETARIO", "SECRETARIA", "SECRETARY" -> "SECRETARIO";
            case "SPECIALIST", "ESPECIALISTA" -> "ESPECIALISTA";
            case "AUXILIAR" -> "AUXILIAR";
            default -> "PACIENTE";
        };
    }
}