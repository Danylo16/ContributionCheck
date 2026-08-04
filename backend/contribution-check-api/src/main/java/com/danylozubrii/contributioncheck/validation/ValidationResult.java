package com.danylozubrii.contributioncheck.validation;

import java.util.List;

import com.danylozubrii.contributioncheck.dto.ContributionCsvRow;

public record ValidationResult(
        ContributionCsvRow row,
        List<ValidationIssueData> issues
) {

    public boolean isValid() {
        return issues.isEmpty();
    }
}