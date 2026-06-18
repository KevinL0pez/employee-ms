package com.parameta.rrhh.employee.config;

import com.parameta.rrhh.employee.security.JwtAuthorityConverter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.List;

@TestConfiguration
@Import(JwtAuthorityConverter.class)
public class TestJwtDecoderConfig {

    @Bean
    @org.springframework.context.annotation.Primary
    JwtDecoder jwtDecoder() {
        return token -> Jwt.withTokenValue(token)
                .header("alg", "none")
                .claim("sub", "test-user")
                .claim("roles", List.of("RRHH"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}
