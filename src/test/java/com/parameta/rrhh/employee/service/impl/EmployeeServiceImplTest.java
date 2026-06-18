package com.parameta.rrhh.employee.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.parameta.rrhh.employee.client.SoapEmployeeClient;
import com.parameta.rrhh.employee.dto.ValidatedEmployee;
import com.parameta.rrhh.employee.dto.EmployeeRequestDTO;
import com.parameta.rrhh.employee.dto.EmployeeResponseDTO;
import com.parameta.rrhh.employee.dto.PeriodDTO;
import com.parameta.rrhh.employee.mapper.EmployeeMapper;
import com.parameta.rrhh.employee.service.IEmployeeValidationService;
import com.parameta.rrhh.employee.soap.SaveEmployeeResponse;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private IEmployeeValidationService validationService;

    @Mock
    private SoapEmployeeClient soapEmployeeClient;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void shouldOrchestrateValidationSoapAndResponseMapping() {
        EmployeeRequestDTO request = new EmployeeRequestDTO();
        ValidatedEmployee validatedEmployee = new ValidatedEmployee(
                "Juan",
                "Pérez",
                "CC",
                "123456789",
                LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10),
                "Developer",
                5_000_000
        );
        SaveEmployeeResponse soapResponse = new SaveEmployeeResponse(1L, "Employee successfully registered");
        EmployeeResponseDTO expectedResponse = EmployeeResponseDTO.builder()
                .names("Juan")
                .registrationId(1L)
                .build();

        when(validationService.validate(request)).thenReturn(validatedEmployee);
        when(soapEmployeeClient.saveEmployee(validatedEmployee)).thenReturn(soapResponse);
        when(employeeMapper.toResponse(
                eq(validatedEmployee),
                any(PeriodDTO.class),
                any(PeriodDTO.class),
                eq(soapResponse)
        )).thenReturn(expectedResponse);

        EmployeeResponseDTO response = employeeService.registerEmployee(request);

        assertNotNull(response);
        assertEquals(1L, response.getRegistrationId());
        verify(validationService).validate(request);
        verify(soapEmployeeClient).saveEmployee(validatedEmployee);
        verify(employeeMapper).toResponse(
                eq(validatedEmployee),
                any(PeriodDTO.class),
                any(PeriodDTO.class),
                eq(soapResponse)
        );
    }

    @Test
    void shouldDelegateMappingToEmployeeMapper() {
        EmployeeRequestDTO request = new EmployeeRequestDTO();
        ValidatedEmployee validatedEmployee = new ValidatedEmployee(
                "Ana",
                "López",
                "CC",
                "987654321",
                LocalDate.of(1995, 3, 20),
                LocalDate.of(2021, 6, 1),
                "Analyst",
                3_500_000
        );
        SaveEmployeeResponse soapResponse = new SaveEmployeeResponse(2L, "OK");

        when(validationService.validate(any())).thenReturn(validatedEmployee);
        when(soapEmployeeClient.saveEmployee(any())).thenReturn(soapResponse);
        when(employeeMapper.toResponse(any(), any(), any(), any()))
                .thenReturn(EmployeeResponseDTO.builder().registrationId(2L).build());

        employeeService.registerEmployee(request);

        verify(employeeMapper).toResponse(
                eq(validatedEmployee),
                any(PeriodDTO.class),
                any(PeriodDTO.class),
                eq(soapResponse)
        );
    }
}
