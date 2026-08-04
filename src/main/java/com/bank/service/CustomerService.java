package com.bank.service;

import java.util.List;

import com.bank.dto.ChangePasswordRequest;
import com.bank.dto.CustomerProfileResponse;
import com.bank.dto.TransactionResponse;
import com.bank.dto.UpdateProfileRequest;
import com.bank.entity.Account;

public interface CustomerService {

    CustomerProfileResponse getProfile();

    CustomerProfileResponse updateProfile(UpdateProfileRequest request);

    void changePassword(ChangePasswordRequest request);

    List<Account> getMyAccounts();

    List<TransactionResponse> getMyTransactions();
}