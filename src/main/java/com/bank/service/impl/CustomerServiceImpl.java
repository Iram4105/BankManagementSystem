package com.bank.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank.dto.ChangePasswordRequest;
import com.bank.dto.CustomerProfileResponse;
import com.bank.dto.TransactionResponse;
import com.bank.dto.UpdateProfileRequest;
import com.bank.entity.Account;
import com.bank.entity.User;
import com.bank.exception.UserNotFoundException;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import com.bank.service.CustomerService;
import com.bank.util.SecurityUtil;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger =
            LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(
            UserRepository userRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CustomerProfileResponse getProfile() {

        String email = SecurityUtil.getLoggedInUserEmail();

        logger.info("Profile request received for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {

                    logger.warn("User not found: {}", email);

                    return new UserNotFoundException(email);
                });

        logger.info("Profile fetched successfully for user: {}", email);

        return new CustomerProfileResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender()
        );
    }

    @Override
    public CustomerProfileResponse updateProfile(UpdateProfileRequest request) {

        String email = SecurityUtil.getLoggedInUserEmail();

        logger.info("Profile update request received for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {

                    logger.warn("User not found: {}", email);

                    return new UserNotFoundException(email);
                });

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());

        userRepository.save(user);

        logger.info("Profile updated successfully for user: {}", email);

        return new CustomerProfileResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender()
        );
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {

        String email = SecurityUtil.getLoggedInUserEmail();

        logger.info("Password change request received for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {

                    logger.warn("User not found: {}", email);

                    return new UserNotFoundException(email);
                });

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword())) {

            logger.warn("Password change failed. Incorrect old password for user: {}",
                    email);

            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        logger.info("Password changed successfully for user: {}", email);
    }

    @Override
    public List<Account> getMyAccounts() {

        String email = SecurityUtil.getLoggedInUserEmail();

        logger.info("Fetching accounts for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {

                    logger.warn("User not found: {}", email);

                    return new UserNotFoundException(email);
                });

        List<Account> accounts = accountRepository.findByUser(user);

        logger.info("Total accounts found for {} : {}", email, accounts.size());

        return accounts;
    }

    @Override
    public List<TransactionResponse> getMyTransactions() {

        String email = SecurityUtil.getLoggedInUserEmail();

        logger.info("Fetching transactions for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {

                    logger.warn("User not found: {}", email);

                    return new UserNotFoundException(email);
                });

        List<Account> accounts = accountRepository.findByUser(user);

        List<TransactionResponse> transactions = accounts.stream()

                .flatMap(account ->
                        transactionRepository.findByAccount(account).stream())

                .map(transaction -> new TransactionResponse(
                        transaction.getTransactionNumber(),
                        transaction.getTransactionType(),
                        transaction.getAmount(),
                        transaction.getTransactionDate(),
                        transaction.getAccount().getAccountNumber(),
                        transaction.getRemarks()
                ))

                .toList();

        logger.info("Total transactions found for {} : {}",
                email,
                transactions.size());

        return transactions;
    }
}