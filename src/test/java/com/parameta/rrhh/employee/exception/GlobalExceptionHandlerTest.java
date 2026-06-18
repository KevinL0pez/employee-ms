package com.parameta.rrhh.employee.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnBadRequestForValidationException() {
        var response = handler.handleValidation(new ValidationException(List.of("Names must not be blank")));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Validation failed", response.getBody().get("error"));
    }

    @Test
    void shouldReturnConflictForDuplicateEmployee() {
        var response = handler.handleSoap(
                new SoapServiceException("An employee with document number already exists: 123")
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Conflict", response.getBody().get("error"));
    }

    @Test
    void shouldReturnBadGatewayForSoapErrors() {
        var response = handler.handleSoap(new SoapServiceException("SOAP service is unavailable"));

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals(502, response.getBody().get("status"));
        assertEquals("SOAP service error", response.getBody().get("error"));
    }

    @Test
    void shouldReturnUnauthorizedForAuthenticationException() {
        var response = handler.handleAuthentication(new BadCredentialsException("Bad credentials"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().get("status"));
        assertEquals("Unauthorized", response.getBody().get("error"));
        assertEquals("Invalid username or password", response.getBody().get("messages"));
    }

    @Test
    void shouldReturnInternalServerErrorForUnexpectedException() {
        var response = handler.handleGeneric(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().get("status"));
    }
}
