 package com.danylozubrii.contributioncheck.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.danylozubrii.contributioncheck.domain.entity.ImportBatch;

public interface ImportBatchRepository
        extends JpaRepository<ImportBatch, Long> {

    List<ImportBatch> findAllByOrderByCreatedAtDesc();
}