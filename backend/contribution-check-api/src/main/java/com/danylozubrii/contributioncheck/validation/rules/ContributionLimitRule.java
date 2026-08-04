package com.danylozubrii.contributioncheck.validation.rules;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.danylozubrii.contributioncheck.dto.ContributionCsvRow;
import com.danylozubrii.contributioncheck.validation.ContributionValidationRule;
import com.danylozubrii.contributioncheck.validation.ValidationContext;
import com.danylozubrii.contributioncheck.validation.ValidationIssueData;

@Component
public class ContributionLimitRule
        implements ContributionValidationRule {

    @Override
    public List<ValidationIssueData> validate(
            ContributionCsvRow row,
            ValidationContext context
    ) {
        BigDecimal totalContribution =
                row.employeeContribution()
                        .add(row.employerContribution());

        if (totalContribution.compareTo(row.grossSalary()) <= 0) {
            return List.of();
        }

        return List.of(new ValidationIssueData(
                "CONTRIBUTION_EXCEEDS_SALARY",
                "employeeContribution,employerContribution",
                "Total contribution must not exceed gross salary"
        ));
    }
}