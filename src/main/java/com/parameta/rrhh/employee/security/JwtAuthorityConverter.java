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
 * Maps the JWT {@code roles} claim to Spring Security {@link org.springframework.security.core.GrantedAuthority}.
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
        return List.of();
    }
}
