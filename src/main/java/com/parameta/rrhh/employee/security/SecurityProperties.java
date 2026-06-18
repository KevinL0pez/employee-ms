package com.parameta.rrhh.employee.security;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/** Security settings for JWT, users and CORS. */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private final Jwt jwt = new Jwt();
    private final User user = new User();
    private final Cors cors = new Cors();

    @PostConstruct
    void validate() {
        if (!jwt.isLocalMode()) {
            return;
        }
        if (!StringUtils.hasText(jwt.getSecret()) || jwt.getSecret().length() < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET must be set and contain at least 32 characters when JWT_MODE=local"
            );
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new IllegalStateException("APP_PASSWORD must be set when JWT_MODE=local");
        }
    }

    @Getter
    @Setter
    public static class Jwt {
        private String mode = "local";
        private String secret;
        private String issuer = "parameta-employee-ms";
        private int expirationMinutes = 60;

        public boolean isLocalMode() {
            return "local".equalsIgnoreCase(mode);
        }

        public boolean isCognitoMode() {
            return "cognito".equalsIgnoreCase(mode);
        }
    }

    @Getter
    @Setter
    public static class User {
        private String username = "rrhh";
        private String password;
    }

    @Getter
    @Setter
    public static class Cors {
        private List<String> allowedOrigins = new ArrayList<>(List.of("*"));
    }
}
