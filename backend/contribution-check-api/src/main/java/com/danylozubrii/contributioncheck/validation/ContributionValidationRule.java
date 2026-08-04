package com.danylozubrii.contributioncheck.validation;

import java.util.List;

import com.danylozubrii.contributioncheck.dto.ContributionCsvRow;

public interface ContributionValidationRule {

    List<ValidationIssueData> validate(
            ContributionCsvRow row,
            ValidationContext context
    );
}