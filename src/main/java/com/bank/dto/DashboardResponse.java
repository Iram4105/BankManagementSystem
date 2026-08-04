package com.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DashboardResponse {

    private long totalUsers;
    private long totalAccounts;
    private long totalTransactions;
}