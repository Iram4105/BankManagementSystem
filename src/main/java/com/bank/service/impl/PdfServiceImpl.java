package com.bank.service.impl;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.exception.AccountNotFoundException;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.service.PdfService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PdfServiceImpl implements PdfService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public PdfServiceImpl(AccountRepository accountRepository,
                          TransactionRepository transactionRepository) {

        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public byte[] generateBankStatement(String accountNumber) {

        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new AccountNotFoundException(accountNumber));

        List<Transaction> transactions =
                transactionRepository.findByAccount(account);

        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            Document document = new Document();

            PdfWriter.getInstance(document, outputStream);

            document.open();

            // ================= Title =================
            Font titleFont =
                    new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD);

            Font subTitleFont =
                    new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);

            Paragraph bankTitle =
                    new Paragraph(
                            "BANK MANAGEMENT SYSTEM",
                            titleFont);

            bankTitle.setAlignment(Element.ALIGN_CENTER);

            document.add(bankTitle);

            Paragraph statementTitle =
                    new Paragraph(
                            "OFFICIAL ACCOUNT STATEMENT",
                            subTitleFont);

            statementTitle.setAlignment(Element.ALIGN_CENTER);

            document.add(statementTitle);

            document.add(new Paragraph(
                    "========================================================"));


            // ================= Customer Details =================

            document.add(new Paragraph(
                    "Customer Name : "
                            + account.getUser().getFirstName()
                            + " "
                            + account.getUser().getLastName()));

            document.add(new Paragraph(
                    "Account Number : "
                            + account.getAccountNumber()));

            document.add(new Paragraph(
                    "Account Type : "
                            + account.getAccountType()));

            document.add(new Paragraph(
                    "Current Balance : ₹"
                            + account.getBalance()));

            document.add(new Paragraph(
                    "Generated On : "
                            + LocalDate.now()));

            document.add(new Paragraph(" "));

            // ================= Transaction Table =================

            PdfPTable table = new PdfPTable(4);

            table.setWidthPercentage(100);

            table.addCell(new PdfPCell(new Phrase("Transaction No")));
            table.addCell(new PdfPCell(new Phrase("Type")));
            table.addCell(new PdfPCell(new Phrase("Amount")));
            table.addCell(new PdfPCell(new Phrase("Date")));

            for (Transaction transaction : transactions) {

                table.addCell(transaction.getTransactionNumber());

                table.addCell(
                        transaction.getTransactionType().name());

                table.addCell("₹" + transaction.getAmount());

                table.addCell(
                        transaction.getTransactionDate().toString());
            }

            document.add(table);

            document.add(new Paragraph(" "));

            Paragraph footer =
                    new Paragraph(
                            "Thank you for banking with us.");

            footer.setAlignment(Element.ALIGN_CENTER);

            document.add(footer);

            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to generate bank statement PDF.", e);
        }
    }
}