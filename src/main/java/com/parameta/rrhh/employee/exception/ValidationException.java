package com.parameta.rrhh.employee.exception;

import java.util.List;
import lombok.Getter;

/** Business validation failure with one or more field messages. */
@Getter
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = errors;
    }
}
