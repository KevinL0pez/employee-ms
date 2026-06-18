package com.parameta.rrhh.employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * JSON body for Login.
 */
@Getter
@Setter
@Schema(description = "Local login credentials")
public class LoginRequestDTO {

    @NotBlank(message = "username must not be blank")
    @Schema(description = "Application username", example = "rrhh")
    private String username;

    @NotBlank(message = "password must not be blank")
    @Schema(description = "Application password", example = "rrhh")
    private String password;
}
