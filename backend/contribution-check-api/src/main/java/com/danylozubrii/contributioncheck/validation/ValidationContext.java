package com.danylozubrii.contributioncheck.validation;

import java.time.YearMonth;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.danylozubrii.contributioncheck.dto.ContributionCsvRow;

public class ValidationContext {

    private final Set<ContributionKey> seenRecords = new HashSet<>();

    public boolean isDuplicate(ContributionCsvRow row) {
        ContributionKey key = new ContributionKey(
                row.employeeId().trim().toUpperCase(Locale.ROOT),
                row.contributionMonth()
        );

        return !seenRecords.add(key);
    }

    private record ContributionKey(
            String employeeId,
            YearMonth contributionMonth
    ) {
    }
}