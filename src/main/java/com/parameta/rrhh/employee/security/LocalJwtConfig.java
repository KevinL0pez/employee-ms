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
 * Registers symmetric JWT encoder/decoder beans for local and Docker environments.
 *
 * <p>Tokens are signed with HS256 using {@link SecurityProperties.Jwt#getSecret()}.
 * The decoder is marked {@link Primary} so it takes precedence over any auto-configured
 * decoder when both profiles could coexist during startup.
 *
 * <p>Disabled when {@code JWT_MODE=cognito}; in AWS, Cognito issues RS256 tokens
 * validated via {@code spring.security.oauth2.resourceserver.jwt.issuer-uri}.
 */
@Configuration
@ConditionalOnProperty(name = "app.security.jwt.mode", havingValue = "local", matchIfMissing = true)
public class LocalJwtConfig {

    /** Validates incoming Bearer tokens on each protected HTTP request. */
    @Bean
    @Primary
    JwtDecoder localJwtDecoder(SecurityProperties securityProperties) {
        return NimbusJwtDecoder.withSecretKey(secretKey(securityProperties)).macAlgorithm(MacAlgorithm.HS256).build();
    }

    /** Signs access tokens issued by {@link LocalAuthService#login(String, String)}. */
    @Bean
    JwtEncoder jwtEncoder(SecurityProperties securityProperties) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey(securityProperties)));
    }

    private SecretKey secretKey(SecurityProperties securityProperties) {
        byte[] secretBytes = securityProperties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(secretBytes, MacAlgorithm.HS256.getName());
    }
}
