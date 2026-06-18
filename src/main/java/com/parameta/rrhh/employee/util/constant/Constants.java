package com.parameta.rrhh.employee.util.constant;

/**
 * JSON keys used in standardized REST error responses across controllers and security filters.
 *
 * <p>All error payloads follow the shape documented by {@link com.parameta.rrhh.employee.dto.ApiErrorResponse}.
 */
public final class Constants {

    private Constants() {
    }

    /** ISO-8601 instant when the error was generated ({@code "timestamp"}). */
    public static final String SYSTEM_TIMESTAMP = "timestamp";

    /** Numeric HTTP status echoed in the body ({@code "status"}). */
    public static final String SYSTEM_STATUS = "status";

    /** Short error category, e.g. {@code "Validation failed"} ({@code "error"}). */
    public static final String SYSTEM_ERROR = "error";

    /** Human-readable details: list of validation errors or a single message ({@code "messages"}). */
    public static final String SYSTEM_MESSAGES = "messages";
}
