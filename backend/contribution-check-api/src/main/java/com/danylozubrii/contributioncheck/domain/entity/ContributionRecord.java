package com.danylozubrii.contributioncheck.domain.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.danylozubrii.contributioncheck.domain.enums.RecordStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "contribution_records")
public class ContributionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false)
    private ImportBatch batch;

    @Column(name = "row_number", nullable = false)
    private int rowNumber;

    @Column(name = "employee_id", nullable = false, length = 100)
    private String employeeId;

    @Column(
            name = "contribution_month",
            nullable = false,
            length = 7
    )
    private String contributionMonth;

    @Column(
            name = "gross_salary",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal grossSalary;

    @Column(
            name = "employee_contribution",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal employeeContribution;

    @Column(
            name = "employer_contribution",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal employerContribution;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecordStatus status;

    @OneToMany(
            mappedBy = "record",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ValidationIssue> issues = new ArrayList<>();

    protected ContributionRecord() {
    }

    public ContributionRecord(
            ImportBatch batch,
            int rowNumber,
            String employeeId,
            String contributionMonth,
            BigDecimal grossSalary,
            BigDecimal employeeContribution,
            BigDecimal employerContribution,
            String currency,
            RecordStatus status
    ) {
        this.batch = batch;
        this.rowNumber = rowNumber;
        this.employeeId = employeeId;
        this.contributionMonth = contributionMonth;
        this.grossSalary = grossSalary;
        this.employeeContribution = employeeContribution;
        this.employerContribution = employerContribution;
        this.currency = currency;
        this.status = status;
    }

    public void addIssue(
            String code,
            String fieldName,
            String message
    ) {
        ValidationIssue issue = new ValidationIssue(
                this,
                code,
                fieldName,
                message
        );

        issues.add(issue);
    }

    public Long getId() {
        return id;
    }

    public ImportBatch getBatch() {
        return batch;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getContributionMonth() {
        return contributionMonth;
    }

    public BigDecimal getGrossSalary() {
        return grossSalary;
    }

    public BigDecimal getEmployeeContribution() {
        return employeeContribution;
    }

    public BigDecimal getEmployerContribution() {
        return employerContribution;
    }

    public String getCurrency() {
        return currency;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public List<ValidationIssue> getIssues() {
        return Collections.unmodifiableList(issues);
    }
}