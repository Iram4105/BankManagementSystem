package com.bank.service;

import java.util.List;

import com.bank.dto.AccountResponse;
import com.bank.dto.DashboardResponse;
import com.bank.entity.User;

public interface AdminService {

    List<User> getAllUsers();

    List<AccountResponse> getAllAccounts();

    void freezeAccount(Long accountId);

    void activateAccount(Long accountId);

    void deleteUser(Long userId);

    DashboardResponse getDashboard();
}	