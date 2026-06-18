package com.parameta.rrhh.employee.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.parameta.rrhh.employee.domain.ValidatedEmployee;
import com.parameta.rrhh.employee.exception.SoapServiceException;
import com.parameta.rrhh.employee.mapper.EmployeeMapper;
import com.parameta.rrhh.employee.soap.EmployeeType;
import com.parameta.rrhh.employee.soap.SaveEmployeeRequest;
import com.parameta.rrhh.employee.soap.SaveEmployeeResponse;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

@ExtendWith(MockitoExtension.class)
class SoapEmployeeClientTest {

    @Mock
    private WebServiceTemplate webServiceTemplate;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private SoapEmployeeClient soapEmployeeClient;

    @Test
    void shouldReturnSoapResponse() {
        ValidatedEmployee employee = sampleEmployee();
        SaveEmployeeResponse expected = new SaveEmployeeResponse(1L, "OK");

        when(employeeMapper.toSoapEmployee(employee)).thenReturn(new EmployeeType());
        when(webServiceTemplate.marshalSendAndReceive(any(SaveEmployeeRequest.class))).thenReturn(expected);

        SaveEmployeeResponse response = soapEmployeeClient.saveEmployee(employee);

        assertEquals(1L, response.getId());
        verify(webServiceTemplate).marshalSendAndReceive(any(SaveEmployeeRequest.class));
    }

    @Test
    void shouldMapSoapFaultToServiceException() {
        ValidatedEmployee employee = sampleEmployee();
        SoapFaultClientException soapFault = org.mockito.Mockito.mock(SoapFaultClientException.class);

        when(employeeMapper.toSoapEmployee(employee)).thenReturn(new EmployeeType());
        when(soapFault.getFaultStringOrReason()).thenReturn("already exists");
        when(webServiceTemplate.marshalSendAndReceive(any(SaveEmployeeRequest.class))).thenThrow(soapFault);

        SoapServiceException exception = assertThrows(
                SoapServiceException.class,
                () -> soapEmployeeClient.saveEmployee(employee)
        );

        assertEquals("already exists", exception.getMessage());
    }

    @Test
    void shouldMapUnavailableService() {
        ValidatedEmployee employee = sampleEmployee();

        when(employeeMapper.toSoapEmployee(employee)).thenReturn(new EmployeeType());
        when(webServiceTemplate.marshalSendAndReceive(any(SaveEmployeeRequest.class)))
                .thenThrow(new WebServiceIOException("Connection refused"));

        SoapServiceException exception = assertThrows(
                SoapServiceException.class,
                () -> soapEmployeeClient.saveEmployee(employee)
        );

        assertEquals(
                "SOAP service is unavailable. Verify that soap-ms is running on the configured URL",
                exception.getMessage()
        );
    }

    @Test
    void shouldFailWhenResponseIsNull() {
        ValidatedEmployee employee = sampleEmployee();

        when(employeeMapper.toSoapEmployee(employee)).thenReturn(new EmployeeType());
        when(webServiceTemplate.marshalSendAndReceive(any(SaveEmployeeRequest.class))).thenReturn(null);

        assertThrows(SoapServiceException.class, () -> soapEmployeeClient.saveEmployee(employee));
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
