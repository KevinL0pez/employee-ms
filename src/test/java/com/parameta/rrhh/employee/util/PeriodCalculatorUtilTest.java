package com.parameta.rrhh.employee.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PeriodCalculatorUtilTest {

    @Test
    void shouldCalculatePeriod() {
        LocalDate from = LocalDate.now().minusYears(25).minusMonths(2).minusDays(5);
        var period = PeriodCalculatorUtil.calculate(from);
        assertEquals(25, period.getYears());
        assertEquals(2, period.getMonths());
        assertEquals(5, period.getDays());
    }
}
