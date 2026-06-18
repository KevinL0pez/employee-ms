package com.parameta.rrhh.employee.domain;

import java.time.LocalDate;

/**
 * Employee data after syntactic and business validation.
 */
public record ValidatedEmployee(
        String names,
        String lastNames,
        String typeDocument,
        String documentNumber,
        LocalDate dateOfBirth,
        LocalDate dateAffiliationCompany,
        String position,
        double salary
) {}
