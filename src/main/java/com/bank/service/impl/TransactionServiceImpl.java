package com.bank.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.TransactionRequest;
import com.bank.dto.TransactionResponse;
import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.enums.TransactionType;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.InsufficientBalanceException;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.service.TransactionService;

import com.bank.enums.AccountStatus;
import com.bank.exception.AccountFrozenException;
import com.bank.exception.InvalidAmountException;
import com.bank.exception.SameAccountTransferException;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger =
            LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  AccountRepository accountRepository) {

        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public TransactionResponse deposit(TransactionRequest request) {

        logger.info("Deposit request received. Account: {}, Amount: {}",
                request.getAccountNumber(),
                request.getAmount());
        validateAmount(request.getAmount());

        Account account = accountRepository
                .findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() ->
                        new AccountNotFoundException(request.getAccountNumber()));
        validateAccountStatus(account);
        
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionNumber(generateTransactionNumber());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAccount(account);
        transaction.setRemarks(request.getRemarks());

        transactionRepository.save(transaction);

        logger.info(
                "Deposit successful. Transaction: {}, Account: {}, Amount: {}",
                transaction.getTransactionNumber(),
                account.getAccountNumber(),
                request.getAmount());

        return mapToResponse(transaction, "Amount deposited successfully");
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(TransactionRequest request) {

        logger.info("Withdrawal request received. Account: {}, Amount: {}",
                request.getAccountNumber(),
                request.getAmount());
        validateAmount(request.getAmount());
        
        Account account = accountRepository
                .findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() ->
                        new AccountNotFoundException(request.getAccountNumber()));
        validateAccountStatus(account);
        
        if (account.getBalance().compareTo(request.getAmount()) < 0) {

            logger.warn("Withdrawal failed due to insufficient balance. Account: {}",
                    account.getAccountNumber());

            throw new InsufficientBalanceException();
        }

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

        logger.info(
                "Withdrawal successful. Transaction: {}, Amount: {}",
                transaction.getTransactionNumber(),
                request.getAmount());

        return mapToResponse(transaction, "Amount withdrawn successfully");
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransactionRequest request) {

        logger.info(
                "Transfer request. From: {}, To: {}, Amount: {}",
                request.getAccountNumber(),
                request.getDestinationAccountNumber(),
                request.getAmount());
        validateAmount(request.getAmount());

        Account sourceAccount = accountRepository
                .findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() ->
                        new AccountNotFoundException(request.getAccountNumber()));

        Account destinationAccount = accountRepository
                .findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                request.getDestinationAccountNumber()));
        validateAccountStatus(sourceAccount);
        validateAccountStatus(destinationAccount);

        validateTransfer(sourceAccount, destinationAccount);

        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {

            logger.warn(
                    "Transfer failed due to insufficient balance. Source Account: {}",
                    sourceAccount.getAccountNumber());

            throw new InsufficientBalanceException();
        }

        // Update balances
        sourceAccount.setBalance(
                sourceAccount.getBalance().subtract(request.getAmount()));

        destinationAccount.setBalance(
                destinationAccount.getBalance().add(request.getAmount()));

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

        logger.info(
                "Transfer successful. Transaction: {}, From: {}, To: {}, Amount: {}",
                transaction.getTransactionNumber(),
                sourceAccount.getAccountNumber(),
                destinationAccount.getAccountNumber(),
                request.getAmount());

        return mapToResponse(transaction, "Amount transferred successfully");
    }

    @Override
    public List<TransactionResponse> getTransactionHistory(String accountNumber) {

        logger.info("Fetching transaction history for account: {}",
                accountNumber);

        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new AccountNotFoundException(accountNumber));

        return transactionRepository.findByAccount(account)
                .stream()
                .map(transaction ->
                        mapToResponse(transaction, "Success"))
                .toList();
    }

    // ================= Helper Methods =================

    private String generateTransactionNumber() {

        Random random = new Random();

        String transactionNumber;

        do {
            transactionNumber =
                    "TXN" + (100000000L + random.nextInt(900000000));
        } while (transactionRepository
                .findByTransactionNumber(transactionNumber)
                .isPresent());

        return transactionNumber;
    }
    private void validateTransfer(Account source,
            Account destination) {

          if (source.getAccountNumber()
                   .equals(destination.getAccountNumber())) {

              throw new SameAccountTransferException();
        }
    }
    private void validateAccountStatus(Account account) {

        if (account.getStatus() == AccountStatus.FROZEN
                || account.getStatus() == AccountStatus.CLOSED) {

            throw new AccountFrozenException(account.getAccountNumber());
        }
    }
    private void validateAmount(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }
    }

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