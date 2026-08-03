package com.bank.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bank.dto.AccountRequest;
import com.bank.dto.AccountResponse;
import com.bank.entity.Account;
import com.bank.entity.User;
import com.bank.enums.AccountStatus;
import com.bank.repository.AccountRepository;
import com.bank.repository.UserRepository;
import com.bank.service.AccountService;
import com.bank.util.SecurityUtils;
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountServiceImpl(AccountRepository accountRepository,
                              UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AccountResponse createAccount(AccountRequest request) {

        // Temporary: Get first registered user
    	String email = SecurityUtils.getLoggedInUserEmail();

    	User user = userRepository.findByEmail(email)
    	        .orElseThrow(() -> new RuntimeException("User not found"));
        Account account = new Account();

        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        account.setUser(user);

        accountRepository.save(account);

        return mapToResponse(account);
    }

    @Override
    public List<AccountResponse> getAllAccounts() {

        return accountRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponse getAccountById(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return mapToResponse(account);
    }

    // ---------------- Helper Methods ----------------

    private String generateAccountNumber() {

        Random random = new Random();

        String accountNumber;

        do {
            accountNumber = "AC" + (1000000000L + random.nextInt(900000000));
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    private AccountResponse mapToResponse(Account account) {

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getStatus(),
                account.getUser().getFirstName() + " " +
                account.getUser().getLastName()
        );
    }
}