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

@Service
public class ImportBatchService {

    private final ImportBatchRepository repository;
    private final ContributionCsvParser parser;

    public ImportBatchService(
            ImportBatchRepository repository,
            ContributionCsvParser parser
    ) {
        this.repository = repository;
        this.parser = parser;
    }

    @Transactional
    public ImportBatchResponse create(MultipartFile file) {
        validateFile(file);

        ImportBatch batch = repository.save(
                new ImportBatch(file.getOriginalFilename())
        );

        List<ContributionCsvRow> rows = parser.parse(file);

        batch.complete(
                rows.size(),
                rows.size(),
                0
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
            throw new IllegalArgumentException("File must not be empty");
        }

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name is required");
        }

        if (!fileName.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException(
                    "Only CSV files are supported"
            );
        }
    }
}