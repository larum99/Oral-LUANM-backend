package com.dental.clinic.security;

import com.dental.clinic.repository.UserRepository;
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
            jwtService.validate(token).ifPresent(claims -> userRepository.findByEmailIgnoreCase(claims.email()).ifPresent(user -> {
                if (!ACTIVE_STATUS.equals(user.getStatus())) {
                    return;
                }
                String roleAuthority = "ROLE_" + roleName(claims.role());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority(roleAuthority))
                );
                authentication.setDetails(user.getId());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }));
        }

        filterChain.doFilter(request, response);
    }

    private String roleName(String role) {
        return switch (String.valueOf(role).toLowerCase()) {
            case "admin" -> "ADMIN";
            case "secretario", "secretary" -> "SECRETARIO";
            case "specialist", "especialista" -> "ESPECIALISTA";
            case "auxiliar" -> "AUXILIAR";
            default -> "PACIENTE";
        };
    }
}
