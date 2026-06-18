package com.parameta.rrhh.employee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.parameta.rrhh.employee.dto.EmployeeRequestDTO;
import com.parameta.rrhh.employee.exception.ValidationException;
import com.parameta.rrhh.employee.service.impl.EmployeeValidationServiceImpl;
import com.parameta.rrhh.employee.validation.groovy.GroovyEmployeeValidationEngine;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmployeeValidationServiceImplTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-06-18T12:00:00Z"),
            ZoneId.of("UTC")
    );

    private EmployeeValidationServiceImpl validationService;

    @BeforeEach
    void setUp() {
        GroovyEmployeeValidationEngine engine = GroovyEmployeeValidationEngine.forTesting(FIXED_CLOCK);
        validationService = new EmployeeValidationServiceImpl(engine);
    }

    @Test
    void shouldValidateEmployeeSuccessfully() {
        var result = validationService.validate(validRequest());

        assertEquals("Juan", result.names());
        assertEquals("123456789", result.documentNumber());
    }

    @Test
    void shouldRejectMinorEmployee() {
        EmployeeRequestDTO request = validRequest();
        request.setDateOfBirth("2010-01-01");
        request.setDateAffiliationCompany("2024-01-01");

        assertThrows(ValidationException.class, () -> validationService.validate(request));
    }

    @Test
    void shouldRejectEmptyFields() {
        EmployeeRequestDTO request = validRequest();
        request.setNames("");

        assertThrows(ValidationException.class, () -> validationService.validate(request));
    }

    @Test
    void shouldRejectInvalidDateFormat() {
        EmployeeRequestDTO request = validRequest();
        request.setDateOfBirth("15-05-1990");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validationService.validate(request)
        );

        assertEquals(1, exception.getErrors().stream()
                .filter(error -> error.contains("yyyy-MM-dd"))
                .count());
    }

    @Test
    void shouldRejectInvalidSalary() {
        EmployeeRequestDTO request = validRequest();
        request.setSalary("abc");

        assertThrows(ValidationException.class, () -> validationService.validate(request));
    }

    @Test
    void shouldRejectFutureBirthDate() {
        EmployeeRequestDTO request = validRequest();
        request.setDateOfBirth("2030-01-01");
        request.setDateAffiliationCompany("2031-01-01");

        assertThrows(ValidationException.class, () -> validationService.validate(request));
    }

    private EmployeeRequestDTO validRequest() {
        EmployeeRequestDTO request = new EmployeeRequestDTO();
        request.setNames("Juan");
        request.setLastNames("Perez");
        request.setTypeDocument("CC");
        request.setDocumentNumber("123456789");
        request.setDateOfBirth("1990-05-15");
        request.setDateAffiliationCompany("2020-01-10");
        request.setPosition("Developer");
        request.setSalary("5000000");
        return request;
    }
}
