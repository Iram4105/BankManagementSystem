package com.bank.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.service.PdfService;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<byte[]> downloadStatement(
            @PathVariable String accountNumber) {

        byte[] pdf = pdfService.generateBankStatement(accountNumber);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=BankStatement.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}