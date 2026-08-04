package com.danylozubrii.contributioncheck.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.danylozubrii.contributioncheck.dto.ContributionCsvRow;

@Component
public class ContributionValidator {

    private final List<ContributionValidationRule> rules;

    public ContributionValidator(
            List<ContributionValidationRule> rules
    ) {
        this.rules = rules;
    }

    public List<ValidationResult> validate(
            List<ContributionCsvRow> rows
    ) {
        ValidationContext context = new ValidationContext();
        List<ValidationResult> results = new ArrayList<>();

        for (ContributionCsvRow row : rows) {
            List<ValidationIssueData> issues = new ArrayList<>();

            for (ContributionValidationRule rule : rules) {
                issues.addAll(rule.validate(row, context));
            }

            results.add(new ValidationResult(row, issues));
        }

        return results;
    }
}