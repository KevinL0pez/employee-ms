package com.parameta.rrhh.employee.security;

import com.parameta.rrhh.employee.util.constant.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Writes RFC 7807-style JSON error bodies for Spring Security filter-chain failures.
 *
 * <p>Replaces default HTML error pages so API clients always receive JSON:
 * {@code 401 Unauthorized} (missing/invalid token) and {@code 403 Forbidden} (valid token, insufficient role).
 */
@Component
@RequiredArgsConstructor
public class SecurityProblemSupport implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        writeProblem(response, HttpStatus.UNAUTHORIZED, "Unauthorized", "Authentication required or token is invalid");
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        writeProblem(response, HttpStatus.FORBIDDEN, "Forbidden", "Insufficient permissions to access this resource");
    }

    private void writeProblem(HttpServletResponse response, HttpStatus status, String error, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), Map.of(
                Constants.SYSTEM_TIMESTAMP, Instant.now().toString(),
                Constants.SYSTEM_STATUS, status.value(),
                Constants.SYSTEM_ERROR, error,
                Constants.SYSTEM_MESSAGES, message
        ));
    }
}
