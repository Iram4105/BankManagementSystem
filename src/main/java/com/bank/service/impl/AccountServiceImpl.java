package com.bank.service.impl;

import java.math.BigDecimal;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bank.dto.AccountRequest;
import com.bank.dto.AccountResponse;
import com.bank.entity.Account;
import com.bank.entity.User;
import com.bank.enums.AccountStatus;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.UserNotFoundException;
import com.bank.repository.AccountRepository;
import com.bank.repository.UserRepository;
import com.bank.service.AccountService;
import com.bank.util.SecurityUtil;

import jakarta.annotation.PostConstruct;


@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger logger =
            LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountServiceImpl(AccountRepository accountRepository,
                              UserRepository userRepository) {

        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AccountResponse createAccount(AccountRequest request) {

        String email = SecurityUtil.getLoggedInUserEmail();

        logger.info("Account creation request received for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {

                    logger.error("User not found: {}", email);

                    return new UserNotFoundException(email);
                });

        Account account = new Account();

        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        account.setUser(user);

        accountRepository.save(account);

        logger.info(
                "Account created successfully. Account Number: {}, Customer: {}",
                account.getAccountNumber(),
                user.getEmail());

        return mapToResponse(account);
    }

    @Override
    public List<AccountResponse> getAllAccounts() {

        logger.info("Fetching all bank accounts");

        return accountRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponse getAccountById(Long id) {

        logger.info("Fetching account with ID: {}", id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {

                    logger.warn("Account not found with ID: {}", id);

                    return new AccountNotFoundException(String.valueOf(id));
                });

        logger.info("Account retrieved successfully: {}",
                account.getAccountNumber());

        return mapToResponse(account);
    }

    // ================= Helper Methods =================

    private String generateAccountNumber() {

        Random random = new Random();

        String accountNumber;

        do {

            accountNumber =
                    "AC" + (1000000000L + random.nextInt(900000000));

        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
    @PostConstruct
    public void init() {
        System.out.println("✅ AccountServiceImpl bean created");
    }

    private AccountResponse mapToResponse(Account account) {

        return new AccountResponse(

                account.getId(),

                account.getAccountNumber(),

                account.getAccountType(),

                account.getBalance(),

                account.getStatus(),

                account.getUser().getFirstName() + " "
                        + account.getUser().getLastName()
                        
                        
        );
    }
}