package com.bank.exception;

public class SameAccountTransferException extends BusinessException {

    public SameAccountTransferException() {

        super("Source and destination accounts cannot be the same.");
    }
}