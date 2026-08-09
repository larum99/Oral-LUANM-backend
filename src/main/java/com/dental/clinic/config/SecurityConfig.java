package com.dental.clinic.config;

import com.dental.clinic.modules.auth.security.JwtAuthenticationFilter;
import com.dental.clinic.shared.security.SecurityHeadersFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityHeadersFilter securityHeadersFilter;
    private final boolean publicApiDocsEnabled;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          SecurityHeadersFilter securityHeadersFilter,
                          @Value("${app.security.public-docs-enabled:false}") boolean publicApiDocsEnabled) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.securityHeadersFilter = securityHeadersFilter;
        this.publicApiDocsEnabled = publicApiDocsEnabled;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/password-reset/request", "/api/auth/password-reset/confirm").permitAll()
                            .requestMatchers("/actuator/health/**").permitAll();

                    configureApiDocsAccess(auth);

                    auth
                            .requestMatchers("/actuator/info").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/api/patients/**").hasAnyRole("ADMIN", "SECRETARIO", "ESPECIALISTA")
                            .requestMatchers("/api/patients/**").hasAnyRole("ADMIN", "SECRETARIO")
                            .anyRequest().authenticated();
                })
                .addFilterBefore(securityHeadersFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void configureApiDocsAccess(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        if (publicApiDocsEnabled) {
            auth.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();
            return;
        }

        auth.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").hasRole("ADMIN");
    }
}