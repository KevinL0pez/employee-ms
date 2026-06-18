package com.parameta.rrhh.employee.security;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * Type-safe binding of {@code app.security.*} properties from YAML and environment variables.
 *
 * <p>On startup, validates that {@code JWT_SECRET} (≥ 32 chars) and {@code APP_PASSWORD} are present.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private final Jwt jwt = new Jwt();
    private final User user = new User();
    private final Cors cors = new Cors();

    @PostConstruct
    void validate() {
        if (!StringUtils.hasText(jwt.getSecret()) || jwt.getSecret().length() < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET must be set and contain at least 32 characters"
            );
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new IllegalStateException("APP_PASSWORD must be set");
        }
    }

    /** JWT issuer, secret and expiration settings. */
    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private String issuer = "parameta-employee-ms";
        private int expirationMinutes = 60;
    }

    /** Credentials for {@code POST /auth/login} ({@code APP_USER}, {@code APP_PASSWORD}). */
    @Getter
    @Setter
    public static class User {
        private String username = "rrhh";
        private String password;
    }

    /** Allowed browser origins for CORS ({@code CORS_ALLOWED_ORIGINS}). */
    @Getter
    @Setter
    public static class Cors {
        private List<String> allowedOrigins = new ArrayList<>(List.of("*"));
    }
}
