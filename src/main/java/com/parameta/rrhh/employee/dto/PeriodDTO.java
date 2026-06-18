package com.parameta.rrhh.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Elapsed time expressed as years, months and days. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodDTO {

    private int years;
    private int months;
    private int days;
}
