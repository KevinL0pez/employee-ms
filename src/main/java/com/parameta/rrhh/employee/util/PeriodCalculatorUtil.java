package com.parameta.rrhh.employee.util;

import com.parameta.rrhh.employee.dto.PeriodDto;
import java.time.LocalDate;
import java.time.Period;

/**
 * Date period helpers for employee age and affiliation time.
 */
public final class PeriodCalculatorUtil {

    private PeriodCalculatorUtil() {
    }

    /**
     * Calculates the elapsed time from {@code from} until today.
     */
    public static PeriodDto calculate(LocalDate from) {
        Period period = Period.between(from, LocalDate.now());
        return PeriodDto.builder()
                .years(period.getYears())
                .months(period.getMonths())
                .days(period.getDays())
                .build();
    }
}
