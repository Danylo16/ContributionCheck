package com.danylozubrii.contributioncheck.domain.entity;

import java.time.OffsetDateTime;

import com.danylozubrii.contributioncheck.domain.enums.ImportStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "import_batches")
public class ImportBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ImportStatus status = ImportStatus.PROCESSING;

    @Column(name = "total_records", nullable = false)
    private int totalRecords = 0;

    @Column(name = "valid_records", nullable = false)
    private int validRecords = 0;

    @Column(name = "invalid_records", nullable = false)
    private int invalidRecords = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected ImportBatch() {
    }

    public ImportBatch(String fileName) {
        this.fileName = fileName;
    }

    @PrePersist
    void setCreatedAt() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    public void complete(
            int totalRecords,
            int validRecords,
            int invalidRecords
    ) {
        this.totalRecords = totalRecords;
        this.validRecords = validRecords;
        this.invalidRecords = invalidRecords;
        this.status = ImportStatus.COMPLETED;
    }

    public void fail() {
        this.status = ImportStatus.FAILED;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public ImportStatus getStatus() {
        return status;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public int getValidRecords() {
        return validRecords;
    }

    public int getInvalidRecords() {
        return invalidRecords;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}