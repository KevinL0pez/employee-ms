package com.parameta.rrhh.employee.client;

import com.parameta.rrhh.employee.domain.ValidatedEmployee;
import com.parameta.rrhh.employee.exception.SoapServiceException;
import com.parameta.rrhh.employee.mapper.EmployeeMapper;
import com.parameta.rrhh.employee.soap.SaveEmployeeRequest;
import com.parameta.rrhh.employee.soap.SaveEmployeeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

/**
 * SOAP client for employee persistence in {@code soap-ms}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SoapEmployeeClient {

    private final WebServiceTemplate webServiceTemplate;
    private final EmployeeMapper employeeMapper;

    public SaveEmployeeResponse saveEmployee(ValidatedEmployee employee) {
        SaveEmployeeRequest request = new SaveEmployeeRequest(employeeMapper.toSoapEmployee(employee));

        try {
            log.debug("Calling SOAP service for document {}", employee.documentNumber());
            SaveEmployeeResponse response = (SaveEmployeeResponse) webServiceTemplate.marshalSendAndReceive(request);
            if (response == null) {
                throw new SoapServiceException("SOAP service returned no response");
            }
            return response;
        } catch (SoapServiceException ex) {
            throw ex;
        } catch (SoapFaultClientException ex) {
            String faultMessage = ex.getFaultStringOrReason();
            log.warn("SOAP fault for document {}: {}", employee.documentNumber(), faultMessage);
            throw new SoapServiceException(faultMessage, ex);
        } catch (WebServiceIOException | ResourceAccessException ex) {
            log.error("SOAP service unavailable for document {}", employee.documentNumber(), ex);
            throw new SoapServiceException("SOAP service is unavailable. Verify that soap-ms is running on the configured URL", ex);
        } catch (Exception ex) {
            log.error("Unexpected SOAP error for document {}", employee.documentNumber(), ex);
            throw new SoapServiceException(resolveErrorMessage(ex), ex);
        }
    }

    private String resolveErrorMessage(Exception ex) {
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            return "Unable to invoke SOAP service: " + ex.getCause().getMessage();
        }
        return "Unable to invoke SOAP service";
    }
}
