package com.parameta.rrhh.employee.service.impl;

import com.parameta.rrhh.employee.dto.ValidatedEmployee;
import com.parameta.rrhh.employee.dto.EmployeeRequestDTO;
import com.parameta.rrhh.employee.service.IEmployeeValidationService;
import com.parameta.rrhh.employee.validation.groovy.GroovyEmployeeValidationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application service that delegates validation to {@link GroovyEmployeeValidationEngine}.
 *
 * <p>Keeps controllers and orchestration ({@link com.parameta.rrhh.employee.service.impl.EmployeeServiceImpl})
 * free of Groovy-specific APIs. Business rules live in the external script so they can evolve
 * without recompiling Java for every rule change.
 */
@Service
@RequiredArgsConstructor
public class EmployeeValidationServiceImpl implements IEmployeeValidationService {

    private final GroovyEmployeeValidationEngine groovyEmployeeValidationEngine;

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidatedEmployee validate(EmployeeRequestDTO request) {
        return groovyEmployeeValidationEngine.validate(request);
    }
}
