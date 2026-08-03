package com.bank.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.dto.TransactionRequest;
import com.bank.dto.TransactionResponse;
import com.bank.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @RequestBody TransactionRequest request) {

        return ResponseEntity.ok(transactionService.deposit(request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @RequestBody TransactionRequest request) {

        return ResponseEntity.ok(transactionService.withdraw(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @RequestBody TransactionRequest request) {

        return ResponseEntity.ok(transactionService.transfer(request));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>> history(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                transactionService.getTransactionHistory(accountNumber));
    }
}