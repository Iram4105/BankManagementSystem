package com.bank.dto;

import com.bank.enums.AccountType;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {

    @NotNull(message = "Account type is required")
    private AccountType accountType;

}