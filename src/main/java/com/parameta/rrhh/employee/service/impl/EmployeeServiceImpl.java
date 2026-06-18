package com.parameta.rrhh.employee.service.impl;

import com.parameta.rrhh.employee.domain.ValidatedEmployee;
import com.parameta.rrhh.employee.dto.EmployeeRequestDTO;
import com.parameta.rrhh.employee.dto.EmployeeResponse;
import com.parameta.rrhh.employee.dto.PeriodDto;
import com.parameta.rrhh.employee.client.SoapEmployeeClient;
import com.parameta.rrhh.employee.mapper.EmployeeMapper;
import com.parameta.rrhh.employee.service.IEmployeeService;
import com.parameta.rrhh.employee.service.IEmployeeValidationService;
import com.parameta.rrhh.employee.util.PeriodCalculatorUtil;
import com.parameta.rrhh.employee.soap.SaveEmployeeResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Orchestrates validation, SOAP persistence and response enrichment.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements IEmployeeService {

    private final IEmployeeValidationService iEmployeeValidationService;
    private final SoapEmployeeClient soapEmployeeClient;
    private final EmployeeMapper employeeMapper;

    @Override
    public EmployeeResponse registerEmployee(EmployeeRequestDTO requestDTO) {
        Objects.requireNonNull(requestDTO, "Employee request must not be null");

        log.info("Starting employee registration for document {}", maskDocument(requestDTO.getDocumentNumber()));

        ValidatedEmployee employee = iEmployeeValidationService.validate(requestDTO);
        SaveEmployeeResponse soapResponse = soapEmployeeClient.saveEmployee(employee);

        PeriodDto currentAge = PeriodCalculatorUtil.calculate(employee.dateOfBirth());
        PeriodDto bondingTime = PeriodCalculatorUtil.calculate(employee.dateAffiliationCompany());

        EmployeeResponse response = employeeMapper.toResponse(
                employee,
                currentAge,
                bondingTime,
                soapResponse
        );

        log.info("Employee successfully registered with id {}", response.getRegistrationId());
        return response;
    }

    private String maskDocument(String documentNumber) {
        if (documentNumber == null || documentNumber.length() <= 4) {
            return "****";
        }
        return "****" + documentNumber.substring(documentNumber.length() - 4);
    }
}
