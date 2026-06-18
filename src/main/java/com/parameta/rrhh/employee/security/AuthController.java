package com.parameta.rrhh.employee.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Local token endpoint. Not available in AWS/Cognito mode ({@code JWT_MODE=cognito}).
 */
@Tag(name = "Authentication")
@RestController
@RequestMapping("/auth")
@ConditionalOnProperty(name = "app.security.jwt.mode", havingValue = "local", matchIfMissing = true)
public class AuthController {

    private final LocalAuthService localAuthService;

    public AuthController(LocalAuthService localAuthService) {
        this.localAuthService = localAuthService;
    }

    @Operation(summary = "Obtain a JWT access token (local mode only)")
    @PostMapping("/login")
    public LocalAuthService.TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return localAuthService.login(request.getUsername(), request.getPassword());
    }

    @Getter
    @Setter
    public static class LoginRequest {

        @NotBlank(message = "username must not be blank")
        private String username;

        @NotBlank(message = "password must not be blank")
        private String password;
    }
}
