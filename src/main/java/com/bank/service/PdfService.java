package com.bank.service;

public interface PdfService {

    byte[] generateBankStatement(String accountNumber);

}