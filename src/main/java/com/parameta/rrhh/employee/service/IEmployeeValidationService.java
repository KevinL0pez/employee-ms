package com.parameta.rrhh.employee.service;

import com.parameta.rrhh.employee.dto.ValidatedEmployee;
import com.parameta.rrhh.employee.dto.EmployeeRequestDTO;

/**
 * Business validation rules for employee registration.
 */
public interface IEmployeeValidationService {

    /**
     * Applies syntactic and business validations over the request.
     *
     * @param request employee input
     * @return normalized employee ready for SOAP integration
     */
    ValidatedEmployee validate(EmployeeRequestDTO request);
}
