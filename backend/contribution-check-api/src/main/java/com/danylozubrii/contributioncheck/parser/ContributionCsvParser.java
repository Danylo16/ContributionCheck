package com.danylozubrii.contributioncheck.parser;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.danylozubrii.contributioncheck.dto.ContributionCsvRow;
import com.danylozubrii.contributioncheck.exception.CsvParsingException;

@Component
public class ContributionCsvParser {

    public List<ContributionCsvRow> parse(MultipartFile file) {
        try (
                Reader reader = new BufferedReader(
                        new InputStreamReader(
                                file.getInputStream(),
                                UTF_8
                        )
                );
                CSVParser csvParser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setTrim(true)
                        .get()
                        .parse(reader)
        ) {
            List<ContributionCsvRow> rows = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                rows.add(parseRecord(record));
            }

            return rows;
                } catch (IOException | RuntimeException exception) {
            throw new CsvParsingException(
                    "Invalid CSV file: " + exception.getMessage(),
                    exception
            );
        }
    }

    private ContributionCsvRow parseRecord(CSVRecord record) {
        int rowNumber = Math.toIntExact(
                record.getRecordNumber() + 1
        );

        return new ContributionCsvRow(
                rowNumber,
                record.get("employeeId"),
                YearMonth.parse(record.get("contributionMonth")),
                new BigDecimal(record.get("grossSalary")),
                new BigDecimal(record.get("employeeContribution")),
                new BigDecimal(record.get("employerContribution")),
                record.get("currency")
        );
    }
}