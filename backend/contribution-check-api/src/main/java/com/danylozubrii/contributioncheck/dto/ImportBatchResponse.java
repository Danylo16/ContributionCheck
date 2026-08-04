package com.danylozubrii.contributioncheck.dto;

import java.time.OffsetDateTime;

import com.danylozubrii.contributioncheck.domain.entity.ImportBatch;
import com.danylozubrii.contributioncheck.domain.enums.ImportStatus;

public record ImportBatchResponse(
        Long id,
        String fileName,
        ImportStatus status,
        int totalRecords,
        int validRecords,
        int invalidRecords,
        OffsetDateTime createdAt
) {

    public static ImportBatchResponse from(ImportBatch batch) {
        return new ImportBatchResponse(
                batch.getId(),
                batch.getFileName(),
                batch.getStatus(),
                batch.getTotalRecords(),
                batch.getValidRecords(),
                batch.getInvalidRecords(),
                batch.getCreatedAt()
        );
    }
}