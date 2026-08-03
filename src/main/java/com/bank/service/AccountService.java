package com.bank.service;

import java.util.List;

import com.bank.dto.AccountRequest;
import com.bank.dto.AccountResponse;

public interface AccountService {

    AccountResponse createAccount(AccountRequest request);

    List<AccountResponse> getAllAccounts();

    AccountResponse getAccountById(Long id);

}