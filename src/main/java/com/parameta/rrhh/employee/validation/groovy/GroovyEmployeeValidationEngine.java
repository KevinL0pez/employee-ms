package com.parameta.rrhh.employee.validation.groovy;

import com.parameta.rrhh.employee.dto.ValidatedEmployee;
import com.parameta.rrhh.employee.dto.EmployeeRequestDTO;
import com.parameta.rrhh.employee.exception.ValidationException;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

/**
 * Groovy Shell engine that externalizes employee validation rules from Java bytecode.
 *
 * <p>The script {@code validation/employee-validation.groovy} is compiled once when the bean
 * is created and executed per request with {@link GroovyClassLoader}. The engine injects:
 * <ul>
 *   <li>{@code request} — {@link EmployeeRequestDTO} with raw query parameters</li>
 *   <li>{@code today} — current date from an injectable {@link Clock} (testable)</li>
 * </ul>
 *
 * <p>Expected script contract:
 * <ul>
 *   <li>On validation errors: return {@code [errors: List<String>]}</li>
 *   <li>On success: return a {@code Map} with typed fields matching {@link ValidatedEmployee}</li>
 * </ul>
 *
 * <p>Script location is configurable via {@code validation.groovy.script} (default: classpath).
 */
@Slf4j
@Component
public class GroovyEmployeeValidationEngine {

    private final Clock clock;
    private final Resource scriptResource;
    private final Class<? extends Script> scriptClass;

    @Autowired
    public GroovyEmployeeValidationEngine(
            Clock clock,
            ResourceLoader resourceLoader,
            @Value("${validation.groovy.script:classpath:validation/employee-validation.groovy}") String scriptLocation
    ) {
        this.clock = clock;
        this.scriptResource = resourceLoader.getResource(scriptLocation);
        this.scriptClass = compileScriptClass();
    }

    /** Package-private constructor for unit tests with a custom {@link Resource}. */
    GroovyEmployeeValidationEngine(Clock clock, Resource scriptResource) {
        this.clock = clock;
        this.scriptResource = scriptResource;
        this.scriptClass = compileScriptClass();
    }

    /** Factory for tests; uses the default classpath script. */
    public static GroovyEmployeeValidationEngine forTesting(Clock clock) {
        return new GroovyEmployeeValidationEngine(
                clock,
                new DefaultResourceLoader()
                        .getResource("classpath:validation/employee-validation.groovy")
        );
    }

    /**
     * Runs the Groovy script against the incoming request.
     *
     * @param request employee data from the REST layer
     * @return normalized and validated domain object
     * @throws ValidationException when the script returns errors
     * @throws IllegalStateException when the script is missing or returns an invalid structure
     */
    public ValidatedEmployee validate(EmployeeRequestDTO request) {
        try {
            Script script = createScript(request);
            return mapResult(script.run());
        } catch (ValidationException | IllegalStateException ex) {
            throw ex;
        }  catch (Exception ex) {
            log.error("Groovy validation script execution failed", ex);
            throw new IllegalStateException("Unable to execute employee validation script", ex);
        }
    }

    private Script createScript(EmployeeRequestDTO request) throws ReflectiveOperationException {
        Binding binding = new Binding();
        binding.setProperty("request", request);
        binding.setProperty("today", LocalDate.now(clock));

        Script script = scriptClass.getDeclaredConstructor().newInstance();
        script.setBinding(binding);
        return script;
    }

    private ValidatedEmployee mapResult(Object raw) {
        if (!(raw instanceof Map<?, ?> result)) {
            throw new IllegalStateException("Groovy script must return a Map");
        }

        Object errorsObject = result.get("errors");
        if (errorsObject instanceof List<?> errorList && !errorList.isEmpty()) {
            List<String> errors = errorList.stream().map(String::valueOf).toList();
            throw new ValidationException(errors);
        }

        return new ValidatedEmployee(
                (String) result.get("names"),
                (String) result.get("lastNames"),
                (String) result.get("typeDocument"),
                (String) result.get("documentNumber"),
                (LocalDate) result.get("dateOfBirth"),
                (LocalDate) result.get("dateAffiliationCompany"),
                (String) result.get("position"),
                (Double) result.get("salary")
        );
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Script> compileScriptClass() {
        try {
            String scriptContent = readScript();
            try (GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader())) {
                Class<? extends Script> compiled = classLoader.parseClass(scriptContent, "employee-validation.groovy");
                log.info("Loaded Groovy validation script from {}", scriptResource.getDescription());
                return compiled;
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read Groovy validation script", ex);
        }
    }

    private String readScript() throws IOException {
        if (!scriptResource.exists()) {
            throw new IllegalStateException("Groovy validation script not found: " + scriptResource.getDescription());
        }

        try (InputStream inputStream = scriptResource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }
}
