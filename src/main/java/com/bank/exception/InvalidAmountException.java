package com.bank.exception;

public class InvalidAmountException extends BusinessException {

    public InvalidAmountException() {
        super("Transaction amount must be greater than zero.");
    }
}