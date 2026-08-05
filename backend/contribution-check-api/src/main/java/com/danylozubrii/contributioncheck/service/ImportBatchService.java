package com.danylozubrii.contributioncheck.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.danylozubrii.contributioncheck.dto.ContributionRecordResponse;
import com.danylozubrii.contributioncheck.domain.entity.ContributionRecord;
import com.danylozubrii.contributioncheck.domain.entity.ImportBatch;
import com.danylozubrii.contributioncheck.domain.enums.RecordStatus;
import com.danylozubrii.contributioncheck.dto.ContributionCsvRow;
import com.danylozubrii.contributioncheck.dto.ImportBatchResponse;
import com.danylozubrii.contributioncheck.parser.ContributionCsvParser;
import com.danylozubrii.contributioncheck.repository.ContributionRecordRepository;
import com.danylozubrii.contributioncheck.repository.ImportBatchRepository;
import com.danylozubrii.contributioncheck.validation.ContributionValidator;
import com.danylozubrii.contributioncheck.validation.ValidationResult;

@Service
public class ImportBatchService {

    private final ImportBatchRepository batchRepository;
    private final ContributionRecordRepository recordRepository;
    private final ContributionCsvParser parser;
    private final ContributionValidator validator;

    public ImportBatchService(
            ImportBatchRepository batchRepository,
            ContributionRecordRepository recordRepository,
            ContributionCsvParser parser,
            ContributionValidator validator
    ) {
        this.batchRepository = batchRepository;
        this.recordRepository = recordRepository;
        this.parser = parser;
        this.validator = validator;
    }

    @Transactional
    public ImportBatchResponse create(MultipartFile file) {
        validateFile(file);

        String fileName = file.getOriginalFilename();

        ImportBatch batch = batchRepository.save(
                new ImportBatch(fileName)
        );

        List<ContributionCsvRow> rows = parser.parse(file);
        List<ValidationResult> results = validator.validate(rows);

        List<ContributionRecord> records = results.stream()
                .map(result -> createRecord(batch, result))
                .toList();

        recordRepository.saveAll(records);

        int invalidRecords = (int) results.stream()
                .filter(result -> !result.isValid())
                .count();

        int validRecords = results.size() - invalidRecords;

        batch.complete(
                results.size(),
                validRecords,
                invalidRecords
        );

        return ImportBatchResponse.from(batch);
    }

    @Transactional(readOnly = true)
    public List<ImportBatchResponse> findAll() {
        return batchRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ImportBatchResponse::from)
                .toList();
    }

    private ContributionRecord createRecord(
            ImportBatch batch,
            ValidationResult result
    ) {
        ContributionCsvRow row = result.row();

        RecordStatus status = result.isValid()
                ? RecordStatus.VALID
                : RecordStatus.INVALID;

        ContributionRecord record = new ContributionRecord(
                batch,
                row.rowNumber(),
                row.employeeId(),
                row.contributionMonth().toString(),
                row.grossSalary(),
                row.employeeContribution(),
                row.employerContribution(),
                row.currency(),
                status
        );

        result.issues().forEach(issue ->
                record.addIssue(
                        issue.code(),
                        issue.fieldName(),
                        issue.message()
                )
        );

        return record;
    }
    @Transactional(readOnly = true)
    public List<ContributionRecordResponse> findRecords(
            Long batchId
    ) {
        return recordRepository
                .findAllByBatchIdOrderByRowNumber(batchId)
                .stream()
                .map(ContributionRecordResponse::from)
                .toList();
    }

    private void validateFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if (file.isEmpty()) {
            throw new IllegalArgumentException(
                    "File must not be empty"
            );
        }

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "File name is required"
            );
        }

        if (!fileName.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException(
                    "Only CSV files are supported"
            );
        }
    }
}