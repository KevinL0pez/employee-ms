package com.parameta.rrhh.employee.validation.groovy;

import com.parameta.rrhh.employee.domain.ValidatedEmployee;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

/**
 * Executes employee validation rules from an external Groovy script.
 */
@Slf4j
@Component
public class GroovyEmployeeValidationEngine {

    private final Clock clock;
    private final Resource scriptResource;
    private volatile Class<? extends Script> scriptClass;

    @Autowired
    public GroovyEmployeeValidationEngine(
            Clock clock,
            ResourceLoader resourceLoader,
            @Value("${validation.groovy.script:classpath:validation/employee-validation.groovy}") String scriptLocation
    ) {
        this.clock = clock;
        this.scriptResource = resourceLoader.getResource(scriptLocation);
    }

    GroovyEmployeeValidationEngine(Clock clock, Resource scriptResource) {
        this.clock = clock;
        this.scriptResource = scriptResource;
    }

    public static GroovyEmployeeValidationEngine forTesting(Clock clock) {
        return new GroovyEmployeeValidationEngine(
                clock,
                new org.springframework.core.io.DefaultResourceLoader()
                        .getResource("classpath:validation/employee-validation.groovy")
        );
    }

    public ValidatedEmployee validate(EmployeeRequestDTO request) {
        try {
            Script script = createScript(request);
            return mapResult(script.run());
        } catch (ValidationException ex) {
            throw ex;
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Groovy validation script execution failed", ex);
            throw new IllegalStateException("Unable to execute employee validation script", ex);
        }
    }

    private Script createScript(EmployeeRequestDTO request) throws ReflectiveOperationException {
        Binding binding = new Binding();
        binding.setProperty("request", request);
        binding.setProperty("today", LocalDate.now(clock));

        Script script = loadScriptClass().getDeclaredConstructor().newInstance();
        script.setBinding(binding);
        return script;
    }

    @SuppressWarnings("unchecked")
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
    private Class<? extends Script> loadScriptClass() {
        Class<? extends Script> cached = scriptClass;
        if (cached != null) {
            return cached;
        }

        synchronized (this) {
            if (scriptClass != null) {
                return scriptClass;
            }

            try {
                String scriptContent = readScript();
                GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader());
                scriptClass = classLoader.parseClass(scriptContent, "employee-validation.groovy");
                log.info("Loaded Groovy validation script from {}", scriptResource.getDescription());
                return scriptClass;
            } catch (IOException ex) {
                throw new IllegalStateException("Unable to read Groovy validation script", ex);
            }
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
