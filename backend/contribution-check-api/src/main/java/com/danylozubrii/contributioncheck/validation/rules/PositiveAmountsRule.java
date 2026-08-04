package com.danylozubrii.contributioncheck.validation.rules;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.danylozubrii.contributioncheck.dto.ContributionCsvRow;
import com.danylozubrii.contributioncheck.validation.ContributionValidationRule;
import com.danylozubrii.contributioncheck.validation.ValidationContext;
import com.danylozubrii.contributioncheck.validation.ValidationIssueData;

@Component
public class PositiveAmountsRule implements ContributionValidationRule {

    @Override
    public List<ValidationIssueData> validate(
            ContributionCsvRow row,
            ValidationContext context
    ) {
        List<ValidationIssueData> issues = new ArrayList<>();

        if (row.grossSalary().compareTo(BigDecimal.ZERO) <= 0) {
            issues.add(new ValidationIssueData(
                    "INVALID_GROSS_SALARY",
                    "grossSalary",
                    "Gross salary must be greater than zero"
            ));
        }

        if (row.employeeContribution().compareTo(BigDecimal.ZERO) < 0) {
            issues.add(new ValidationIssueData(
                    "NEGATIVE_EMPLOYEE_CONTRIBUTION",
                    "employeeContribution",
                    "Employee contribution must not be negative"
            ));
        }

        if (row.employerContribution().compareTo(BigDecimal.ZERO) < 0) {
            issues.add(new ValidationIssueData(
                    "NEGATIVE_EMPLOYER_CONTRIBUTION",
                    "employerContribution",
                    "Employer contribution must not be negative"
            ));
        }

        return issues;
    }
}