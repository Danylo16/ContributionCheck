package com.danylozubrii.contributioncheck.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.danylozubrii.contributioncheck.domain.entity.ImportBatch;
import com.danylozubrii.contributioncheck.dto.ContributionCsvRow;
import com.danylozubrii.contributioncheck.dto.ImportBatchResponse;
import com.danylozubrii.contributioncheck.parser.ContributionCsvParser;
import com.danylozubrii.contributioncheck.repository.ImportBatchRepository;
import com.danylozubrii.contributioncheck.validation.ContributionValidator;
import com.danylozubrii.contributioncheck.validation.ValidationResult;

@Service
public class ImportBatchService {

    private final ImportBatchRepository repository;
    private final ContributionCsvParser parser;
    private final ContributionValidator validator;

    public ImportBatchService(
            ImportBatchRepository repository,
            ContributionCsvParser parser,
            ContributionValidator validator
    ) {
        this.repository = repository;
        this.parser = parser;
        this.validator = validator;
    }

    @Transactional
    public ImportBatchResponse create(MultipartFile file) {
        validateFile(file);

        String fileName = file.getOriginalFilename();

        ImportBatch batch = repository.save(
                new ImportBatch(fileName)
        );

        List<ContributionCsvRow> rows = parser.parse(file);
        List<ValidationResult> results = validator.validate(rows);

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
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ImportBatchResponse::from)
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