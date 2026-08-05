package com.danylozubrii.contributioncheck.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.danylozubrii.contributioncheck.domain.entity.ContributionRecord;

public interface ContributionRecordRepository
        extends JpaRepository<ContributionRecord, Long> {

    @EntityGraph(attributePaths = "issues")
    List<ContributionRecord> findAllByBatchIdOrderByRowNumber(
            Long batchId
    );
}