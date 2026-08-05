package com.danylozubrii.contributioncheck.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.danylozubrii.contributioncheck.domain.entity.ContributionRecord;
import com.danylozubrii.contributioncheck.domain.enums.RecordStatus;
import com.danylozubrii.contributioncheck.dto.ImportBatchResponse;
import com.danylozubrii.contributioncheck.repository.ContributionRecordRepository;
import com.danylozubrii.contributioncheck.repository.ImportBatchRepository;

@SpringBootTest
@Testcontainers
class ImportBatchServiceIntegrationTest {

    @Container
    static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:17-alpine")
                    .withDatabaseName("contribution_check_test")
                    .withUsername("test_user")
                    .withPassword("test_password");

    @DynamicPropertySource
    static void configureDatabase(
            DynamicPropertyRegistry registry
    ) {
        registry.add(
                "spring.datasource.url",
                POSTGRES::getJdbcUrl
        );
        registry.add(
                "spring.datasource.username",
                POSTGRES::getUsername
        );
        registry.add(
                "spring.datasource.password",
                POSTGRES::getPassword
        );
    }

    @Autowired
    private ImportBatchService service;

    @Autowired
    private ImportBatchRepository batchRepository;

    @Autowired
    private ContributionRecordRepository recordRepository;

    @BeforeEach
    void cleanDatabase() {
        recordRepository.deleteAll();
        batchRepository.deleteAll();
    }

    @Test
    void importsValidAndInvalidRecordsIntoPostgres() {
        String csv = """
                employeeId,contributionMonth,grossSalary,employeeContribution,employerContribution,currency
                EMP-001,2026-06,3500.00,175.00,175.00,EUR
                EMP-002,2026-06,4200.00,-100.00,210.00,EUR
                EMP-003,2026-06,3000.00,150.00,150.00,USD
                EMP-001,2026-06,3500.00,175.00,175.00,EUR
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "contributions.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8)
        );

        ImportBatchResponse response = service.create(file);

        assertEquals(4, response.totalRecords());
        assertEquals(1, response.validRecords());
        assertEquals(3, response.invalidRecords());

        List<ContributionRecord> records =
                recordRepository
                        .findAllByBatchIdOrderByRowNumber(
                                response.id()
                        );

        assertEquals(4, records.size());
        assertEquals(RecordStatus.VALID, records.get(0).getStatus());
        assertEquals(RecordStatus.INVALID, records.get(1).getStatus());
        assertEquals(
                "NEGATIVE_EMPLOYEE_CONTRIBUTION",
                records.get(1).getIssues().getFirst().getCode()
        );
        assertEquals(
                "UNSUPPORTED_CURRENCY",
                records.get(2).getIssues().getFirst().getCode()
        );
        assertEquals(
                "DUPLICATE_RECORD",
                records.get(3).getIssues().getFirst().getCode()
        );
    }
}