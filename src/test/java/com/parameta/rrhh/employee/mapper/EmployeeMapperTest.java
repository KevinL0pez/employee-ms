package com.parameta.rrhh.employee.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.parameta.rrhh.employee.domain.ValidatedEmployee;
import com.parameta.rrhh.employee.dto.PeriodDto;
import com.parameta.rrhh.employee.soap.SaveEmployeeResponse;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class EmployeeMapperTest {

    private final EmployeeMapper mapper = new EmployeeMapper();

    @Test
    void shouldMapToSoapEmployee() {
        ValidatedEmployee employee = sampleEmployee();

        var soapEmployee = mapper.toSoapEmployee(employee);

        assertEquals("Juan", soapEmployee.getNames());
        assertEquals("123456789", soapEmployee.getDocumentNumber());
        assertEquals("1990-05-15", soapEmployee.getDateOfBirth());
        assertEquals(5_000_000, soapEmployee.getSalary());
    }

    @Test
    void shouldMapToResponse() {
        ValidatedEmployee employee = sampleEmployee();
        PeriodDto currentAge = PeriodDto.builder().years(36).months(1).days(2).build();
        PeriodDto affiliationTime = PeriodDto.builder().years(6).months(5).days(7).build();
        SaveEmployeeResponse soapResponse = new SaveEmployeeResponse(10L, "Employee successfully registered");

        var response = mapper.toResponse(employee, currentAge, affiliationTime, soapResponse);

        assertEquals("Juan", response.getNames());
        assertEquals(10L, response.getRegistrationId());
        assertEquals(currentAge, response.getCurrentAge());
        assertEquals("Employee successfully registered", response.getMessage());
    }

    private ValidatedEmployee sampleEmployee() {
        return new ValidatedEmployee(
                "Juan",
                "Perez",
                "CC",
                "123456789",
                LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10),
                "Developer",
                5_000_000
        );
    }
}
