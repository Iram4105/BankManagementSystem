package com.bank.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.dto.ChangePasswordRequest;
import com.bank.dto.CustomerProfileResponse;
import com.bank.dto.TransactionResponse;
import com.bank.dto.UpdateProfileRequest;
import com.bank.entity.Account;
import com.bank.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileResponse> getProfile() {

        return ResponseEntity.ok(customerService.getProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<CustomerProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {

        return ResponseEntity.ok(
                customerService.updateProfile(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        customerService.changePassword(request);

        return ResponseEntity.ok("Password Changed Successfully");
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAccounts() {

        return ResponseEntity.ok(customerService.getMyAccounts());
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions() {

        return ResponseEntity.ok(
                customerService.getMyTransactions());
    }
}