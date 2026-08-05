package com.danylozubrii.contributioncheck.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "validation_issues")
public class ValidationIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "record_id", nullable = false)
    private ContributionRecord record;

    @Column(nullable = false, length = 100)
    private String code;

    @Column(name = "field_name", length = 100)
    private String fieldName;

    @Column(nullable = false, length = 500)
    private String message;

    protected ValidationIssue() {
    }

    public ValidationIssue(
            ContributionRecord record,
            String code,
            String fieldName,
            String message
    ) {
        this.record = record;
        this.code = code;
        this.fieldName = fieldName;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public ContributionRecord getRecord() {
        return record;
    }

    public String getCode() {
        return code;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }
}