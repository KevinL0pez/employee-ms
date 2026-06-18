package com.parameta.rrhh.employee.service.impl;

import com.parameta.rrhh.employee.domain.ValidatedEmployee;
import com.parameta.rrhh.employee.dto.EmployeeRequestDTO;
import com.parameta.rrhh.employee.service.IEmployeeValidationService;
import com.parameta.rrhh.employee.validation.groovy.GroovyEmployeeValidationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Delegates employee validation to the Groovy Shell script.
 */
@Service
@RequiredArgsConstructor
public class EmployeeValidationServiceImpl implements IEmployeeValidationService {

    private final GroovyEmployeeValidationEngine groovyEmployeeValidationEngine;

    @Override
    public ValidatedEmployee validate(EmployeeRequestDTO request) {
        return groovyEmployeeValidationEngine.validate(request);
    }
}
