package com.parameta.rrhh.employee.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * CSRF configuration for the stateless REST API.
 *
 * <p>Spring Security CSRF mitigates attacks where a browser <em>automatically</em> sends
 * credentials (session cookies, HTTP basic) on cross-site requests. This service does not
 * use that model:
 *
 * <ul>
 *   <li>{@link org.springframework.security.config.http.SessionCreationPolicy#STATELESS} —
 *       no server-side session or session cookie.</li>
 *   <li>Protected endpoints authenticate via {@code Authorization: Bearer <JWT>}; the
 *       browser never attaches that header unless client code explicitly sets it.</li>
 *   <li>{@code POST /auth/login} accepts JSON credentials and returns a token; it does not
 *       establish a cookie-based session.</li>
 * </ul>
 *
 * <p>Disabling CSRF is therefore safe for this architecture and is the recommended setup
 * for OAuth2 resource servers with Bearer tokens.
 */
public final class StatelessCsrfConfigurer {

    private StatelessCsrfConfigurer() {
    }

    /**
     * Disables CSRF because authentication is not cookie-based.
     */
    @SuppressWarnings("java:S4502")
    public static void disable(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
    }
}
