package com.parameta.rrhh.employee.dto;

import com.parameta.rrhh.employee.util.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * OpenAPI schema for standardized error responses returned by REST controllers.
 *
 * @see Constants
 */
@Getter
@Setter
@Schema(description = "Standard API error response")
public class ApiErrorResponse {

    @Schema(
            description = "ISO-8601 timestamp when the error occurred",
            example = "2026-06-18T12:00:00Z",
            name = Constants.SYSTEM_TIMESTAMP
    )
    private String timestamp;

    @Schema(
            description = "HTTP status code",
            example = "400",
            name = Constants.SYSTEM_STATUS
    )
    private int status;

    @Schema(
            description = "Short error category",
            example = "Validation failed",
            name = Constants.SYSTEM_ERROR
    )
    private String error;

    @Schema(
            description = "Validation errors as a list, or a single integration or security message",
            example = "[\"Date Of Birth must not be a future date\"]",
            name = Constants.SYSTEM_MESSAGES
    )
    private List<String> messages;

}
