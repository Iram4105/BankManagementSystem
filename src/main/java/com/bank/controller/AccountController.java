package com.bank.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.bank.dto.AccountRequest;
import com.bank.dto.AccountResponse;
import com.bank.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Create Account
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Validated @RequestBody AccountRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(request));
    }

    // Get All Accounts
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {

        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    // Get Account By Id
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(
            @PathVariable Long id) {

        return ResponseEntity.ok(accountService.getAccountById(id));
    }
}