package com.parameta.rrhh.employee.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * Issues HS256-signed JWT access tokens for the local authentication flow.
 *
 * <p>Flow: credentials are validated by Spring Security's {@link AuthenticationManager};
 * on success a JWT is built with {@code sub}, {@code iss}, {@code iat}, {@code exp}
 * and a custom {@code roles} claim. The token is returned to the client and is
 * <strong>not</strong> stored server-side (stateless session).
 *
 * <p>Active only when {@code JWT_MODE=local} (default). In Cognito mode this bean
 * is not registered and clients obtain tokens directly from AWS.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.jwt.mode", havingValue = "local", matchIfMissing = true)
public class LocalAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final SecurityProperties securityProperties;

    /**
     * Authenticates the user and returns a Bearer access token.
     *
     * @param username value from {@code APP_USER} (default {@code rrhh})
     * @param password value from {@code APP_PASSWORD}
     * @return token payload compatible with OAuth2 client expectations
     * @throws org.springframework.security.core.AuthenticationException if credentials are invalid
     */
    public TokenResponse login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        Instant now = Instant.now();
        SecurityProperties.Jwt jwtSettings = securityProperties.getJwt();
        Instant expiresAt = now.plus(jwtSettings.getExpirationMinutes(), ChronoUnit.MINUTES);

        List<String> roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtSettings.getIssuer())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(authentication.getName())
                .claim("roles", roles)
                .build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)
        ).getTokenValue();
        long expiresInSeconds = ChronoUnit.SECONDS.between(now, expiresAt);

        return new TokenResponse(token, "Bearer", expiresInSeconds);
    }

    /**
     * OAuth2-style token response. The client must persist {@link #accessToken()} and
     * send it on subsequent requests as {@code Authorization: Bearer <token>}.
     *
     * @param accessToken signed JWT string
     * @param tokenType   always {@code Bearer}
     * @param expiresIn   seconds until {@code exp} claim
     */
    public record TokenResponse(String accessToken, String tokenType, long expiresIn) {}
}
