package com.parameta.rrhh.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** API response after successful employee registration. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {

    private String names;
    private String lastNames;
    private String typeDocument;
    private String documentNumber;
    private String dateOfBirth;
    private String dateAffiliationCompany;
    private String position;
    private Double salary;
    private PeriodDTO currentAge;
    private PeriodDTO affiliationTime;
    private Long registrationId;
    private String message;
}
