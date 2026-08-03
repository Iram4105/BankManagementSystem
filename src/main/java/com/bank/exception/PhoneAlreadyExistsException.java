package com.bank.exception;

public class PhoneAlreadyExistsException extends RuntimeException {

    public PhoneAlreadyExistsException(String phone) {
        super("Phone number already exists: " + phone);
    }
}