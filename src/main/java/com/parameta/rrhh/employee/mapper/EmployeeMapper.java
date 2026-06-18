package com.parameta.rrhh.employee.mapper;

import com.parameta.rrhh.employee.domain.ValidatedEmployee;
import com.parameta.rrhh.employee.dto.EmployeeResponse;
import com.parameta.rrhh.employee.dto.PeriodDto;
import com.parameta.rrhh.employee.soap.EmployeeType;
import com.parameta.rrhh.employee.soap.SaveEmployeeResponse;
import org.springframework.stereotype.Component;

/**
 * Maps domain and SOAP models to API and integration contracts.
 */
@Component
public class EmployeeMapper {

    public EmployeeType toSoapEmployee(ValidatedEmployee employee) {
        return new EmployeeType(
                employee.names(),
                employee.lastNames(),
                employee.typeDocument(),
                employee.documentNumber(),
                employee.dateOfBirth().toString(),
                employee.dateAffiliationCompany().toString(),
                employee.position(),
                employee.salary()
        );
    }

    public EmployeeResponse toResponse(
            ValidatedEmployee employee,
            PeriodDto currentAge,
            PeriodDto affiliationTime,
            SaveEmployeeResponse soapResponse
    ) {
        return EmployeeResponse.builder()
                .names(employee.names())
                .lastNames(employee.lastNames())
                .typeDocument(employee.typeDocument())
                .documentNumber(employee.documentNumber())
                .dateOfBirth(employee.dateOfBirth().toString())
                .dateAffiliationCompany(employee.dateAffiliationCompany().toString())
                .position(employee.position())
                .salary(employee.salary())
                .currentAge(currentAge)
                .affiliationTime(affiliationTime)
                .registrationId(soapResponse.getId())
                .message(soapResponse.getMessage())
                .build();
    }
}
