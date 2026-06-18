package com.parameta.rrhh.employee.exception;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.parameta.rrhh.employee.util.constant.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps application and integration exceptions to HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleBeanValidation(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(Map.of(
                Constants.SYSTEM_TIMESTAMP, Instant.now().toString(),
                Constants.SYSTEM_STATUS, HttpStatus.BAD_REQUEST.value(),
                Constants.SYSTEM_ERROR, "Validation failed",
                Constants.SYSTEM_MESSAGES, messages
        ));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(ValidationException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                Constants.SYSTEM_TIMESTAMP, Instant.now().toString(),
                Constants.SYSTEM_STATUS, HttpStatus.BAD_REQUEST.value(),
                Constants.SYSTEM_ERROR, "Validation failed",
                Constants.SYSTEM_MESSAGES, ex.getErrors()
        ));
    }

    @ExceptionHandler(SoapServiceException.class)
    public ResponseEntity<Map<String, Object>> handleSoap(SoapServiceException ex) {
        HttpStatus status = isDuplicateEmployeeError(ex.getMessage())
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_GATEWAY;

        return ResponseEntity.status(status).body(Map.of(
                Constants.SYSTEM_TIMESTAMP, Instant.now().toString(),
                Constants.SYSTEM_STATUS, status.value(),
                Constants.SYSTEM_ERROR, status == HttpStatus.CONFLICT ? "Conflict" : "SOAP service error",
                Constants.SYSTEM_MESSAGES, ex.getMessage()
        ));
    }

    private boolean isDuplicateEmployeeError(String message) {
        return message != null && message.contains("already exists");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                Constants.SYSTEM_TIMESTAMP, Instant.now().toString(),
                Constants.SYSTEM_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Constants.SYSTEM_ERROR, "Internal error",
                Constants.SYSTEM_MESSAGES, ex.getMessage()
        ));
    }
}
