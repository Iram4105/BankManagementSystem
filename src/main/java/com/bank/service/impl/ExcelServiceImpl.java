package com.bank.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.exception.AccountNotFoundException;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.service.ExcelService;

@Service
public class ExcelServiceImpl implements ExcelService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public ExcelServiceImpl(AccountRepository accountRepository,
                            TransactionRepository transactionRepository) {

        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public byte[] exportTransactions(String accountNumber) {

        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new AccountNotFoundException(accountNumber));

        List<Transaction> transactions =
                transactionRepository.findByAccount(account);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Transactions");

            // ================= Header Row =================

            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("Transaction No");
            header.createCell(1).setCellValue("Type");
            header.createCell(2).setCellValue("Amount");
            header.createCell(3).setCellValue("Date");
            header.createCell(4).setCellValue("Remarks");

            // ================= Data Rows =================

            int rowNum = 1;

            for (Transaction transaction : transactions) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0)
                        .setCellValue(transaction.getTransactionNumber());

                row.createCell(1)
                        .setCellValue(transaction.getTransactionType().name());

                row.createCell(2)
                        .setCellValue(transaction.getAmount().doubleValue());

                row.createCell(3)
                        .setCellValue(transaction.getTransactionDate().toString());

                row.createCell(4)
                        .setCellValue(transaction.getRemarks());
            }

            // ================= Auto Size Columns =================

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);

            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to export transactions to Excel.", e);
        }
    }
}