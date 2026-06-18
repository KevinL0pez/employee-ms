package com.parameta.rrhh.employee.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.parameta.rrhh.employee.config.TestJwtDecoderConfig;
import com.parameta.rrhh.employee.config.TestWebSecurityConfig;
import com.parameta.rrhh.employee.security.JwtAuthorityConverter;
import com.parameta.rrhh.employee.dto.EmployeeResponse;
import com.parameta.rrhh.employee.dto.PeriodDto;
import com.parameta.rrhh.employee.exception.GlobalExceptionHandler;
import com.parameta.rrhh.employee.service.IEmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
@Import({GlobalExceptionHandler.class, TestWebSecurityConfig.class, TestJwtDecoderConfig.class, JwtAuthorityConverter.class})
@ActiveProfiles("test")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IEmployeeService employeeService;

    @Test
    void shouldReturnEmployeeResponse() throws Exception {
        when(employeeService.registerEmployee(any())).thenReturn(
                EmployeeResponse.builder()
                        .names("Juan")
                        .registrationId(1L)
                        .currentAge(PeriodDto.builder().years(36).months(0).days(0).build())
                        .build()
        );

        mockMvc.perform(get("/employee/validate")
                        .header("Authorization", "Bearer test-token")
                        .param("names", "Juan")
                        .param("lastNames", "Perez")
                        .param("typeDocument", "CC")
                        .param("documentNumber", "123456789")
                        .param("dateOfBirth", "1990-05-15")
                        .param("dateAffiliationCompany", "2020-01-10")
                        .param("position", "Developer")
                        .param("salary", "5000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").value("Juan"))
                .andExpect(jsonPath("$.registrationId").value(1));
    }

    @Test
    void shouldReturnUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/employee/validate")
                        .param("names", "Juan")
                        .param("lastNames", "Perez")
                        .param("typeDocument", "CC")
                        .param("documentNumber", "123456789")
                        .param("dateOfBirth", "1990-05-15")
                        .param("dateAffiliationCompany", "2020-01-10")
                        .param("position", "Developer")
                        .param("salary", "5000000"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestWhenRequiredFieldIsMissing() throws Exception {
        mockMvc.perform(get("/employee/validate")
                        .header("Authorization", "Bearer test-token")
                        .param("lastNames", "Perez")
                        .param("typeDocument", "CC")
                        .param("documentNumber", "123456789")
                        .param("dateOfBirth", "1990-05-15")
                        .param("dateAffiliationCompany", "2020-01-10")
                        .param("position", "Developer")
                        .param("salary", "5000000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
