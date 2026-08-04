package com.danylozubrii.contributioncheck.validation;

public record ValidationIssueData(
        String code,
        String fieldName,
        String message
) {
}