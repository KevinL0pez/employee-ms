package com.parameta.rrhh.employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** Query parameters for employee validation and registration. */
@Getter
@Setter
public class EmployeeRequestDTO {

    @NotBlank(message = "Names must not be blank")
    @Schema(description = "Employee first names", example = "Juan")
    private String names;

    @NotBlank(message = "Last Names must not be blank")
    @Schema(description = "Employee last names", example = "Pérez")
    private String lastNames;

    @NotBlank(message = "Type Document must not be blank")
    @Schema(description = "Type of document code", example = "CC")
    private String typeDocument;

    @NotBlank(message = "Document Number must not be blank")
    @Schema(description = "Employee document number", example = "1002445566")
    private String documentNumber;

    @NotBlank(message = "Date Of Birth must not be blank")
    @Schema(description = "Employee date of birth. Expected format: yyyy-MM-dd", example = "2001-08-29")
    private String dateOfBirth;

    @NotBlank(message = "Date Affiliation Company must not be blank")
    @Schema(description = "Company affiliation date. Expected format: yyyy-MM-dd", example = "2020-06-29")
    private String dateAffiliationCompany;

    @NotBlank(message = "Position must not be blank")
    @Schema(description = "Employee position code", example = "DEVELOPER")
    private String position;

    @NotBlank(message = "Salary must not be blank")
    @Schema(description = "Employee salary", example = "5500000.00")
    private String salary;
}
