package com.parameta.rrhh.employee.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

/**
 * Bridges JWT claims to Spring Security {@link org.springframework.security.core.GrantedAuthority}.
 *
 * <p>Supports two token issuers without code changes in controllers:
 * <ul>
 *   <li><strong>Local:</strong> reads the {@code roles} claim (e.g. {@code ["RRHH"]})</li>
 *   <li><strong>Cognito:</strong> reads {@code cognito:groups} and maps them to {@code ROLE_*} authorities</li>
 * </ul>
 *
 * <p>Used by the OAuth2 resource server configured in {@link SecurityConfig}.
 */
@Component
public class JwtAuthorityConverter {

    /**
     * Builds the converter wired into {@code oauth2ResourceServer().jwt()}.
     */
    public JwtAuthenticationConverter authenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter());
        return converter;
    }

    private Converter<Jwt, Collection<GrantedAuthority>> grantedAuthoritiesConverter() {
        return jwt -> extractRoles(jwt).stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    private List<String> extractRoles(Jwt jwt) {
        Object rolesClaim = jwt.getClaim("roles");
        if (rolesClaim instanceof List<?> roles) {
            return roles.stream().map(String::valueOf).toList();
        }
        if (rolesClaim instanceof String role) {
            return List.of(role);
        }
        if (jwt.hasClaim("cognito:groups")) {
            return jwt.getClaimAsStringList("cognito:groups");
        }
        return List.of();
    }
}
