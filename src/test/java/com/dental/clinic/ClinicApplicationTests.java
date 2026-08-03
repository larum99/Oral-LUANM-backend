package com.dental.clinic;

import com.dental.clinic.dto.request.LoginRequest;
import com.dental.clinic.dto.response.AuthUserResponse;
import com.dental.clinic.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
        "app.security.jwt.secret=test-secret-for-jwt-authentication-32-chars",
        "app.security.jwt.expiration-minutes=480"
})
class ClinicApplicationTests {

    @Autowired
    private AuthService authService;

    @Test
    void contextLoads() {
    }

    @Test
    void seededAdminCanLogin() {
        AuthUserResponse response = authService.login(
                new LoginRequest("admin@admin.com", "12345678")
        );

        assertNotNull(response.id());
        assertEquals("admin@admin.com", response.email());
        assertEquals("Admin Local", response.name());
        assertEquals("admin", response.role());
        assertEquals("registro/admin/", response.path());
        assertNotNull(response.token());
        assertEquals("Bearer", response.tokenType());
    }
}
