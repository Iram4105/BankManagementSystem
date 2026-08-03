package com.bank.dto;

import java.math.BigDecimal;

import com.bank.enums.AccountStatus;
import com.bank.enums.AccountType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountResponse {

    private Long id;

    private String accountNumber;

    private AccountType accountType;

    private BigDecimal balance;

    private AccountStatus status;

    private String customerName;
}