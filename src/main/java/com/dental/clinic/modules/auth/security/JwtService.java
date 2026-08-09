package com.dental.clinic.modules.auth.security;

import com.dental.clinic.modules.auth.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final byte[] secret;
    private final long expirationMinutes;

    public JwtService(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.expiration-minutes}") long expirationMinutes
    ) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 caracteres.");
        }
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(User user, String frontendRole) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expirationMinutes * 60);
        String header = base64Url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = base64Url("{" +
                "\"sub\":\"" + escape(user.getEmail().toLowerCase(Locale.ROOT)) + "\"," +
                "\"uid\":" + user.getId() + "," +
                "\"role\":\"" + escape(frontendRole) + "\"," +
                "\"iat\":" + now.getEpochSecond() + "," +
                "\"exp\":" + expiresAt.getEpochSecond() +
                "}");
        return header + "." + payload + "." + sign(header + "." + payload);
    }

    public long getExpirationMinutes() {
        return expirationMinutes;
    }

    public Optional<JwtClaims> validate(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return Optional.empty();
            String unsignedToken = parts[0] + "." + parts[1];
            if (!constantTimeEquals(sign(unsignedToken), parts[2])) return Optional.empty();
            String json = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Map<String, String> claims = parseFlatJson(json);
            long exp = Long.parseLong(claims.getOrDefault("exp", "0"));
            if (Instant.now().getEpochSecond() >= exp) return Optional.empty();
            return Optional.of(new JwtClaims(claims.get("sub"), claims.get("role"), Long.parseLong(claims.getOrDefault("uid", "0"))));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("No fue posible firmar el JWT.", ex);
        }
    }

    private String base64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private boolean constantTimeEquals(String left, String right) {
        return MessageDigestSupport.equals(left.getBytes(StandardCharsets.UTF_8), right.getBytes(StandardCharsets.UTF_8));
    }

    private String escape(String value) {
        return String.valueOf(value == null ? "" : value).replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private Map<String, String> parseFlatJson(String json) {
        Map<String, String> result = new LinkedHashMap<>();
        String body = json.trim();
        if (body.startsWith("{") && body.endsWith("}")) {
            body = body.substring(1, body.length() - 1);
        }
        for (String pair : body.split(",")) {
            String[] parts = pair.split(":", 2);
            if (parts.length != 2) continue;
            String key = unquote(parts[0].trim());
            String value = unquote(parts[1].trim());
            result.put(key, value);
        }
        return result;
    }

    private String unquote(String value) {
        String clean = value.trim();
        if (clean.startsWith("\"") && clean.endsWith("\"")) {
            clean = clean.substring(1, clean.length() - 1);
        }
        return clean.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    public record JwtClaims(String email, String role, Long userId) {}
}
