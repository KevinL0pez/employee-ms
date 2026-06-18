package com.parameta.rrhh.employee.exception;

/** Error returned by the SOAP integration layer. */
public class SoapServiceException extends RuntimeException {

    public SoapServiceException(String message) {
        super(message);
    }

    public SoapServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
