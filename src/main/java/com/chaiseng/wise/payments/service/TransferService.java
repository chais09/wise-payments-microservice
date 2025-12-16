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


        TransferResponse resp = new TransferResponse();
//

        //
        return resp;
    }

}
