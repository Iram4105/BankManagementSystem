package com.bank.service;

import java.util.List;

import com.bank.dto.TransactionRequest;
import com.bank.dto.TransactionResponse;

public interface TransactionService {

    TransactionResponse deposit(TransactionRequest request);

    TransactionResponse withdraw(TransactionRequest request);

    TransactionResponse transfer(TransactionRequest request);

    List<TransactionResponse> getTransactionHistory(String accountNumber);

}