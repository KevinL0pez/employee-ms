package com.parameta.rrhh.employee.validation.groovy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.parameta.rrhh.employee.dto.EmployeeRequestDTO;
import com.parameta.rrhh.employee.exception.ValidationException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

class GroovyEmployeeValidationEngineTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-06-18T12:00:00Z"),
            ZoneId.of("UTC")
    );

    private GroovyEmployeeValidationEngine engine;

    @BeforeEach
    void setUp() {
        engine = GroovyEmployeeValidationEngine.forTesting(FIXED_CLOCK);
    }

    @Test
    void shouldCacheCompiledScriptAcrossInvocations() {
        EmployeeRequestDTO request = validRequest();
        request.setDocumentNumber("111");

        var first = engine.validate(request);
        request.setDocumentNumber("222");
        var second = engine.validate(request);

        assertEquals("111", first.documentNumber());
        assertEquals("222", second.documentNumber());
    }

    @Test
    void shouldFailWhenScriptIsMissing() {
        assertThrows(IllegalStateException.class, () -> new GroovyEmployeeValidationEngine(
                FIXED_CLOCK,
                new DefaultResourceLoader().getResource("classpath:validation/missing-script.groovy")
        ));
    }

    private EmployeeRequestDTO validRequest() {
        EmployeeRequestDTO request = new EmployeeRequestDTO();
        request.setNames("Ana");
        request.setLastNames("Lopez");
        request.setTypeDocument("CC");
        request.setDocumentNumber("999");
        request.setDateOfBirth("1990-05-15");
        request.setDateAffiliationCompany("2020-01-10");
        request.setPosition("Developer");
        request.setSalary("5000000");
        return request;
    }
}
