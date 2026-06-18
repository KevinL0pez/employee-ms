package com.parameta.rrhh.employee.security;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

/**
 * HMAC-signed JWT beans for local and Docker environments.
 * Disabled when {@code JWT_MODE=cognito} (AWS uses Cognito JWKS).
 */
@Configuration
@ConditionalOnProperty(name = "app.security.jwt.mode", havingValue = "local", matchIfMissing = true)
public class LocalJwtConfig {

    @Bean
    @Primary
    JwtDecoder localJwtDecoder(SecurityProperties securityProperties) {
        return NimbusJwtDecoder.withSecretKey(secretKey(securityProperties)).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    JwtEncoder jwtEncoder(SecurityProperties securityProperties) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey(securityProperties)));
    }

    private SecretKey secretKey(SecurityProperties securityProperties) {
        byte[] secretBytes = securityProperties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(secretBytes, MacAlgorithm.HS256.getName());
    }
}
