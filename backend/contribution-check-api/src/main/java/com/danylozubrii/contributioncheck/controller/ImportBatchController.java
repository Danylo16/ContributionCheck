package com.danylozubrii.contributioncheck.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.danylozubrii.contributioncheck.dto.ImportBatchResponse;
import com.danylozubrii.contributioncheck.service.ImportBatchService;

@RestController
@RequestMapping("/api/imports")
public class ImportBatchController {

    private final ImportBatchService service;

    public ImportBatchController(ImportBatchService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ImportBatchResponse upload(
            @RequestParam("file") MultipartFile file
    ) {
        return service.create(file);
    }

    @GetMapping
    public List<ImportBatchResponse> findAll() {
        return service.findAll();
    }
}