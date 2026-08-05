package com.danylozubrii.contributioncheck.dto;

import java.math.BigDecimal;
import java.util.List;

import com.danylozubrii.contributioncheck.domain.entity.ContributionRecord;
import com.danylozubrii.contributioncheck.domain.enums.RecordStatus;

public record ContributionRecordResponse(
        Long id,
        int rowNumber,
        String employeeId,
        String contributionMonth,
        BigDecimal grossSalary,
        BigDecimal employeeContribution,
        BigDecimal employerContribution,
        String currency,
        RecordStatus status,
        List<ValidationIssueResponse> issues
) {

    public static ContributionRecordResponse from(
            ContributionRecord record
    ) {
        List<ValidationIssueResponse> issues =
                record.getIssues()
                        .stream()
                        .map(ValidationIssueResponse::from)
                        .toList();

        return new ContributionRecordResponse(
                record.getId(),
                record.getRowNumber(),
                record.getEmployeeId(),
                record.getContributionMonth(),
                record.getGrossSalary(),
                record.getEmployeeContribution(),
                record.getEmployerContribution(),
                record.getCurrency(),
                record.getStatus(),
                issues
        );
    }
}