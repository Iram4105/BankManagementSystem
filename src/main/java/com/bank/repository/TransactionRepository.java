package com.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.entity.Account;
import com.bank.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionNumber(String transactionNumber);

    List<Transaction> findByAccount(Account account);

}