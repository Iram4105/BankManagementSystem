package com.bank.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bank.dto.AccountResponse;
import com.bank.dto.DashboardResponse;
import com.bank.entity.Account;
import com.bank.entity.User;
import com.bank.enums.AccountStatus;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.UserNotFoundException;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import com.bank.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger =
            LoggerFactory.getLogger(AdminServiceImpl.class);

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            AccountRepository accountRepository,
                            TransactionRepository transactionRepository) {

        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<User> getAllUsers() {

        logger.info("Fetching all registered users");

        return userRepository.findAll();
    }

    @Override
    public List<AccountResponse> getAllAccounts() {

        logger.info("Fetching all bank accounts");

        return accountRepository.findAll()
                .stream()
                .map(account -> new AccountResponse(
                        account.getId(),
                        account.getAccountNumber(),
                        account.getAccountType(),
                        account.getBalance(),
                        account.getStatus(),
                        account.getUser().getFirstName() + " "
                                + account.getUser().getLastName()
                ))
                .toList();
    }

    @Override
    public void freezeAccount(Long accountId) {

        logger.info("Freeze account request received. Account ID: {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    logger.warn("Account not found. ID: {}", accountId);
                    return new AccountNotFoundException(String.valueOf(accountId));
                });

        account.setStatus(AccountStatus.FROZEN);
        accountRepository.save(account);

        logger.info("Account frozen successfully. Account Number: {}",
                account.getAccountNumber());
    }

    @Override
    public void activateAccount(Long accountId) {

        logger.info("Activate account request received. Account ID: {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    logger.warn("Account not found. ID: {}", accountId);
                    return new AccountNotFoundException(String.valueOf(accountId));
                });

        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);

        logger.info("Account activated successfully. Account Number: {}",
                account.getAccountNumber());
    }

    @Override
    public void deleteUser(Long userId) {

        logger.info("Delete user request received. User ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found. ID: {}", userId);
                    return new UserNotFoundException(String.valueOf(userId));
                });

        userRepository.delete(user);

        logger.info("User deleted successfully. Email: {}", user.getEmail());
    }

    @Override
    public DashboardResponse getDashboard() {

        logger.info("Loading admin dashboard");

        long totalUsers = userRepository.count();
        long totalAccounts = accountRepository.count();
        long totalTransactions = transactionRepository.count();

        logger.info(
                "Dashboard Loaded -> Users: {}, Accounts: {}, Transactions: {}",
                totalUsers,
                totalAccounts,
                totalTransactions);

        return new DashboardResponse(
                totalUsers,
                totalAccounts,
                totalTransactions
        );
    }
}