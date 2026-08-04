package com.bank.exception;

public class AccountFrozenException extends BusinessException {

    public AccountFrozenException(String accountNumber) {

        super("Account is frozen: " + accountNumber);
    }
}