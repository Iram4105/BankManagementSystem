package com.bank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bank.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransactionResponse {

    private String transactionNumber;

    private TransactionType transactionType;

    private BigDecimal amount;

    private LocalDateTime transactionDate;

    private String accountNumber;

    private String message;
}