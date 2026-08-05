package com.danylozubrii.contributioncheck.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.danylozubrii.contributioncheck.dto.ContributionCsvRow;
import com.danylozubrii.contributioncheck.validation.rules.ContributionLimitRule;
import com.danylozubrii.contributioncheck.validation.rules.CurrencyRule;
import com.danylozubrii.contributioncheck.validation.rules.DuplicateRecordRule;
import com.danylozubrii.contributioncheck.validation.rules.PositiveAmountsRule;

class ContributionValidatorTest {

    private ContributionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ContributionValidator(List.of(
                new PositiveAmountsRule(),
                new CurrencyRule(),
                new ContributionLimitRule(),
                new DuplicateRecordRule()
        ));
    }

    @Test
    void acceptsValidRecord() {
        ContributionCsvRow row = createRow(
                2,
                "EMP-001",
                "3500.00",
                "175.00",
                "175.00",
                "EUR"
        );

        ValidationResult result =
                validator.validate(List.of(row)).getFirst();

        assertTrue(result.isValid());
        assertTrue(result.issues().isEmpty());
    }

    @Test
    void rejectsNegativeEmployeeContribution() {
        ContributionCsvRow row = createRow(
                2,
                "EMP-001",
                "3500.00",
                "-175.00",
                "175.00",
                "EUR"
        );

        ValidationResult result =
                validator.validate(List.of(row)).getFirst();

        assertEquals(1, result.issues().size());
        assertEquals(
                "NEGATIVE_EMPLOYEE_CONTRIBUTION",
                result.issues().getFirst().code()
        );
    }

    @Test
    void rejectsUnsupportedCurrency() {
        ContributionCsvRow row = createRow(
                2,
                "EMP-001",
                "3500.00",
                "175.00",
                "175.00",
                "USD"
        );

        ValidationResult result =
                validator.validate(List.of(row)).getFirst();

        assertEquals(1, result.issues().size());
        assertEquals(
                "UNSUPPORTED_CURRENCY",
                result.issues().getFirst().code()
        );
    }

    @Test
    void rejectsContributionAboveSalary() {
        ContributionCsvRow row = createRow(
                2,
                "EMP-001",
                "100.00",
                "60.00",
                "50.00",
                "EUR"
        );

        ValidationResult result =
                validator.validate(List.of(row)).getFirst();

        assertEquals(1, result.issues().size());
        assertEquals(
                "CONTRIBUTION_EXCEEDS_SALARY",
                result.issues().getFirst().code()
        );
    }

    @Test
    void marksSecondEmployeeMonthCombinationAsDuplicate() {
        ContributionCsvRow first = createRow(
                2,
                "EMP-001",
                "3500.00",
                "175.00",
                "175.00",
                "EUR"
        );

        ContributionCsvRow duplicate = createRow(
                3,
                "EMP-001",
                "3500.00",
                "175.00",
                "175.00",
                "EUR"
        );

        List<ValidationResult> results =
                validator.validate(List.of(first, duplicate));

        assertTrue(results.get(0).isValid());

        assertEquals(
                "DUPLICATE_RECORD",
                results.get(1).issues().getFirst().code()
        );
    }

    private ContributionCsvRow createRow(
            int rowNumber,
            String employeeId,
            String grossSalary,
            String employeeContribution,
            String employerContribution,
            String currency
    ) {
        return new ContributionCsvRow(
                rowNumber,
                employeeId,
                YearMonth.of(2026, 6),
                new BigDecimal(grossSalary),
                new BigDecimal(employeeContribution),
                new BigDecimal(employerContribution),
                currency
        );
    }
}