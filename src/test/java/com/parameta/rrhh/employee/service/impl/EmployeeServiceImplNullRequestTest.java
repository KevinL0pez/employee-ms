package com.parameta.rrhh.employee.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.parameta.rrhh.employee.mapper.EmployeeMapper;
import com.parameta.rrhh.employee.client.SoapEmployeeClient;
import com.parameta.rrhh.employee.service.IEmployeeValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplNullRequestTest {

    @Mock
    private IEmployeeValidationService validationService;

    @Mock
    private SoapEmployeeClient soapEmployeeClient;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void shouldRejectNullRequest() {
        assertThrows(NullPointerException.class, () -> employeeService.registerEmployee(null));
    }
}
