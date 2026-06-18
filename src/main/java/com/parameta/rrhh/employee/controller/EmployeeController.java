package com.parameta.rrhh.employee.controller;

import com.parameta.rrhh.employee.dto.EmployeeRequestDTO;
import com.parameta.rrhh.employee.dto.EmployeeResponse;
import com.parameta.rrhh.employee.service.IEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entry point for employee validation and registration.
 */
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
    @PreAuthorize("hasRole('RRHH')")
    @GetMapping("/validate")
    public EmployeeResponse registerEmployee(
            @Parameter(description = "Employee data for validation and registration", required = true)
            @Valid EmployeeRequestDTO requestDTO
    ) {
        return iEmployeeService.registerEmployee(requestDTO);
    }
}
