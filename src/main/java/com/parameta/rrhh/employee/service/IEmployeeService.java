package com.parameta.rrhh.employee.service;

import com.parameta.rrhh.employee.dto.EmployeeRequestDTO;
import com.parameta.rrhh.employee.dto.EmployeeResponseDTO;

/**
 * Application service for employee registration.
 */
public interface IEmployeeService {

    /**
     * Validates, registers and enriches an employee request.
     *
     * @param requestDTO incoming employee data
     * @return registration result with age and affiliation time
     */
    EmployeeResponseDTO registerEmployee(EmployeeRequestDTO requestDTO);
}
