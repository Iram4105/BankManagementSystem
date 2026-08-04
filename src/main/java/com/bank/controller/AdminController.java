package com.bank.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.dto.AccountResponse;
import com.bank.dto.DashboardResponse;
import com.bank.entity.User;
import com.bank.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(adminService.getAllAccounts());
    }

    @PutMapping("/accounts/{id}/freeze")
    public ResponseEntity<String> freezeAccount(@PathVariable Long id) {

        adminService.freezeAccount(id);
        return ResponseEntity.ok("Account Frozen Successfully");
    }

    @PutMapping("/accounts/{id}/activate")
    public ResponseEntity<String> activateAccount(@PathVariable Long id) {

        adminService.activateAccount(id);
        return ResponseEntity.ok("Account Activated Successfully");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {

        adminService.deleteUser(id);
        return ResponseEntity.ok("User Deleted Successfully");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard() {

        return ResponseEntity.ok(adminService.getDashboard());
    }
}