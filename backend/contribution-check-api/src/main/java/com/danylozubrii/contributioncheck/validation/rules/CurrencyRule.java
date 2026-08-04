package com.danylozubrii.contributioncheck.validation.rules;

import java.util.List;

import org.springframework.stereotype.Component;

import com.danylozubrii.contributioncheck.dto.ContributionCsvRow;
import com.danylozubrii.contributioncheck.validation.ContributionValidationRule;
import com.danylozubrii.contributioncheck.validation.ValidationContext;
import com.danylozubrii.contributioncheck.validation.ValidationIssueData;

@Component
public class CurrencyRule implements ContributionValidationRule {

    @Override
    public List<ValidationIssueData> validate(
            ContributionCsvRow row,
            ValidationContext context
    ) {
        if ("EUR".equalsIgnoreCase(row.currency())) {
            return List.of();
        }

        return List.of(new ValidationIssueData(
                "UNSUPPORTED_CURRENCY",
                "currency",
                "Only EUR is supported"
        ));
    }
}