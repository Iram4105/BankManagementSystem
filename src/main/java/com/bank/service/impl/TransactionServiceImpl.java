package com.bank.service.impl;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.bank.dto.TransactionRequest;
import com.bank.dto.TransactionResponse;
import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bank.enums.TransactionType;
import com.bank.exception.InsufficientBalanceException;
import com.bank.exception.AccountNotFoundException;


@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  AccountRepository accountRepository) {

        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public TransactionResponse deposit(TransactionRequest request) {

        // Find Account
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Increase Balance
        account.setBalance(account.getBalance().add(request.getAmount()));

        // Save Updated Account
        accountRepository.save(account);

        // Create Transaction
        Transaction transaction = new Transaction();

        transaction.setTransactionNumber(generateTransactionNumber());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAccount(account);
        transaction.setRemarks(request.getRemarks());

        transactionRepository.save(transaction);

        return mapToResponse(transaction, "Amount deposited successfully");
    }
    @Override
    public TransactionResponse withdraw(TransactionRequest request) {

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(request.getAccountNumber()));
        // Check Balance
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Deduct Balance
        account.setBalance(account.getBalance().subtract(request.getAmount()));

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setTransactionNumber(generateTransactionNumber());
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAccount(account);
        transaction.setRemarks(request.getRemarks());

        transactionRepository.save(transaction);

        return mapToResponse(transaction, "Amount withdrawn successfully");
    }

    @Override
    public TransactionResponse transfer(TransactionRequest request) {

        Account sourceAccount = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(request.getAccountNumber()));
        Account destinationAccount = accountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() ->  new AccountNotFoundException(request.getDestinationAccountNumber()));
        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
        	throw new InsufficientBalanceException();
        // Update balances
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        destinationAccount.setBalance(destinationAccount.getBalance().add(request.getAmount()));

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        // Record transaction
        Transaction transaction = new Transaction();

        transaction.setTransactionNumber(generateTransactionNumber());
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAccount(sourceAccount);
        transaction.setRemarks(request.getRemarks());

        transactionRepository.save(transaction);
        
        return mapToResponse(transaction, "Amount transferred successfully");
    }
 
    @Override
    public List<TransactionResponse> getTransactionHistory(String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return transactionRepository.findByAccount(account)
                .stream()
                .map(transaction -> mapToResponse(transaction, "Success"))
                .toList();
    }
 // Generate Unique Transaction Number
    private String generateTransactionNumber() {

        Random random = new Random();

        String transactionNumber;

        do {
            transactionNumber = "TXN" + (100000000L + random.nextInt(900000000));
        } while (transactionRepository.findByTransactionNumber(transactionNumber).isPresent());

        return transactionNumber;
    }

    // Convert Entity to DTO
    private TransactionResponse mapToResponse(
            Transaction transaction,
            String message) {

        return new TransactionResponse(
                transaction.getTransactionNumber(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getAccount().getAccountNumber(),
                message
        );
    }

}