package com.danylozubrii.contributioncheck.dto;

import com.danylozubrii.contributioncheck.domain.entity.ValidationIssue;

public record ValidationIssueResponse(
        Long id,
        String code,
        String fieldName,
        String message
) {

    public static ValidationIssueResponse from(
            ValidationIssue issue
    ) {
        return new ValidationIssueResponse(
                issue.getId(),
                issue.getCode(),
                issue.getFieldName(),
                issue.getMessage()
        );
    }
}