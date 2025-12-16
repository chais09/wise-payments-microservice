package com.chaiseng.wise.payments.service;

import com.chaiseng.wise.payments.dto.TransferRequest;
import com.chaiseng.wise.payments.dto.TransferResponse;
import com.chaiseng.wise.payments.entity.Account;
import com.chaiseng.wise.payments.entity.Transaction;
import com.chaiseng.wise.payments.entity.TransactionType;
import com.chaiseng.wise.payments.exception.BadRequestException;
import com.chaiseng.wise.payments.exception.InsufficientFundsException;
import com.chaiseng.wise.payments.exception.NotFoundException;
import com.chaiseng.wise.payments.repository.AccountRepository;
import com.chaiseng.wise.payments.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TransferService {
    private final AccountRepository accountRepo;
    private final TransactionRepository txRepo;
    private final IdempotencyService idempotencyService;

    public TransferService(AccountRepository accountRepo,
                           TransactionRepository txRepo,
                           IdempotencyService idempotencyService) {
        this.accountRepo = accountRepo;
        this.txRepo = txRepo;
        this.idempotencyService = idempotencyService;
    }

    @Transactional
    public TransferResponse transfer(TransferRequest req) {
        if(req.getFromAccountId().equals(req.getToAccountId())) {
            throw new BadRequestException("fromAccountId and toAccountId cannot be the same");
        }
        if(req.getAmount() == null || req.getAmount().signum() < 0) {
            throw new BadRequestException("amount cannot be negative");
        }


        Account from = accountRepo.findById(req.getFromAccountId())
                .orElseThrow(() -> new NotFoundException("From account not found: " + req.getFromAccountId()));
        Account to = accountRepo.findById(req.getToAccountId())
                .orElseThrow(() -> new NotFoundException("To account not found: " + req.getToAccountId()));

        //check if balance is enough from accountfrom


        //check the currency see if both account is the same
        if (!from.getCurrency().equals(req.getCurrency()) || !to.getCurrency().equals(req.getCurrency())) {
            throw new BadRequestException("Currency mismatch for accounts");
        }

        BigDecimal beforeFrom = from.getBalance();
        if (beforeFrom.compareTo(req.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance for account " + from.getId());
        }
        BigDecimal afterFrom = beforeFrom.subtract(req.getAmount());

        BigDecimal beforeTo = to.getBalance();
        BigDecimal afterTo = beforeTo.add(req.getAmount());

        // apply updates
        from.setBalance(afterFrom);
        to.setBalance(afterTo);
        accountRepo.save(from);
        accountRepo.save(to);

        // correlation id links the debit and credit
        UUID correlationId = UUID.randomUUID();

        Transaction debit = Transaction.builder()
                .accountId(from.getId())
                .type(TransactionType.TRANSFER_DEBIT)
                .amount(req.getAmount())
                .currency(req.getCurrency())
                .balanceBefore(beforeFrom)
                .balanceAfter(afterFrom)
                .correlationId(correlationId)
                .build();

        Transaction credit = Transaction.builder()
                .accountId(to.getId())
                .type(TransactionType.TRANSFER_CREDIT)
                .amount(req.getAmount())
                .currency(req.getCurrency())
                .balanceBefore(beforeTo)
                .balanceAfter(afterTo)
                .correlationId(correlationId)
                .build();

        txRepo.save(debit);
        txRepo.save(credit);

        TransferResponse resp = new TransferResponse();
        resp.setTransferId(correlationId);
        resp.setFromAccountId(from.getId());
        resp.setToAccountId(to.getId());
        resp.setAmount(req.getAmount());
        resp.setCurrency(req.getCurrency());
        resp.setStatus("COMPLETED");
        resp.setCreatedAt(OffsetDateTime.now());

        return resp;
    }

}
