package com.parameta.rrhh.employee.controller;

import com.parameta.rrhh.employee.dto.ApiErrorResponse;
import com.parameta.rrhh.employee.dto.LoginRequestDTO;
import com.parameta.rrhh.employee.security.LocalAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entry point for obtaining JWT access tokens.
 *
 * <p>{@code POST /auth/login} validates credentials and returns a Bearer token for
 * {@code /employee/**} endpoints.
 */
@Tag(name = "Authentication")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LocalAuthService localAuthService;

    public AuthController(LocalAuthService localAuthService) {
        this.localAuthService = localAuthService;
    }

    /**
     * Validates credentials and returns a signed JWT. Does not create server-side sessions.
     */
    @Operation(summary = "Obtain a JWT access token")
    @ApiResponse(
            responseCode = "200",
            description = "JWT issued successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LocalAuthService.TokenResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid login request body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Invalid username or password",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class)
            )
    )
    @PostMapping("/login")
    public LocalAuthService.TokenResponse login(@Valid @RequestBody LoginRequestDTO request) {
        return localAuthService.login(request.getUsername(), request.getPassword());
    }

}
