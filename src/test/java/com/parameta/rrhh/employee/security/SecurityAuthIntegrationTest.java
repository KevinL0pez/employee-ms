package com.parameta.rrhh.employee.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parameta.rrhh.employee.dto.EmployeeResponseDTO;
import com.parameta.rrhh.employee.dto.LoginRequestDTO;
import com.parameta.rrhh.employee.dto.PeriodDTO;
import com.parameta.rrhh.employee.service.IEmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityAuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IEmployeeService employeeService;

    @BeforeEach
    void setUp() {
        when(employeeService.registerEmployee(any())).thenReturn(
                EmployeeResponseDTO.builder()
                        .names("Juan")
                        .registrationId(1L)
                        .currentAge(PeriodDTO.builder().years(36).months(0).days(0).build())
                        .build()
        );
    }

    @Test
    void shouldRejectEmployeeEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/employee/validate")
                        .param("names", "Juan")
                        .param("lastNames", "Perez")
                        .param("typeDocument", "CC")
                        .param("documentNumber", "123456789")
                        .param("dateOfBirth", "1990-05-15")
                        .param("dateAffiliationCompany", "2020-01-10")
                        .param("position", "Developer")
                        .param("salary", "5000000"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void shouldLoginAndAccessProtectedEndpointWithToken() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("rrhh");
        loginRequest.setPassword("rrhh");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("accessToken")
                .asText();

        mockMvc.perform(get("/employee/validate")
                        .header("Authorization", "Bearer " + token)
                        .param("names", "Juan")
                        .param("lastNames", "Perez")
                        .param("typeDocument", "CC")
                        .param("documentNumber", "9990001112")
                        .param("dateOfBirth", "1990-05-15")
                        .param("dateAffiliationCompany", "2020-01-10")
                        .param("position", "Developer")
                        .param("salary", "5000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").value("Juan"));
    }
}
