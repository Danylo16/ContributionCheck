package com.danylozubrii.contributioncheck.dto;

import java.math.BigDecimal;
import java.time.YearMonth;

public record ContributionCsvRow(
        String employeeId,
        YearMonth contributionMonth,
        BigDecimal grossSalary,
        BigDecimal employeeContribution,
        BigDecimal employerContribution,
        String currency
) {
}