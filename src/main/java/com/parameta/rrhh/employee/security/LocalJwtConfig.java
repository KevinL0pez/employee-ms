package com.parameta.rrhh.employee.security;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

/**
 * Registers HS256 JWT encoder and decoder beans.
 *
 * <p>Tokens are signed with {@link SecurityProperties.Jwt#getSecret()}.
 */
@Configuration
public class LocalJwtConfig {

    /** Validates incoming Bearer tokens on each protected HTTP request. */
    @Bean
    JwtDecoder jwtDecoder(SecurityProperties securityProperties) {
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
