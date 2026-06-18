package com.parameta.rrhh.employee.controller;

import com.parameta.rrhh.employee.dto.ApiErrorResponse;
import com.parameta.rrhh.employee.dto.EmployeeRequestDTO;
import com.parameta.rrhh.employee.dto.EmployeeResponseDTO;
import com.parameta.rrhh.employee.service.IEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entry point for employee validation and registration.
 */
@Tag(name = "Employees")
@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final IEmployeeService iEmployeeService;

    /**
     * Validates input, persists the employee through SOAP and returns the enriched response.
     */
    @Operation(
            summary = "Validates and registers an employee",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Employee validated and registered successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = EmployeeResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Request or business validation failed",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Missing or invalid JWT",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "403",
            description = "Authenticated user lacks RRHH role",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "409",
            description = "Employee document already exists",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "502",
            description = "SOAP integration error or service unavailable",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "Unexpected server error",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class)
            )
    )
    @PreAuthorize("hasRole('RRHH')")
    @GetMapping("/validate")
    public EmployeeResponseDTO registerEmployee(
            @Parameter(description = "Employee data for validation and registration", required = true)
            @Valid EmployeeRequestDTO requestDTO
    ) {
        return iEmployeeService.registerEmployee(requestDTO);
    }
}
