package com.chaiseng.wise.payments.service;

import com.chaiseng.wise.payments.dto.CreateAccountRequest;
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
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository repo;
    private final TransactionRepository txRepo;

    public AccountService(AccountRepository repo, TransactionRepository txRepo) {
        this.repo = repo;
        this.txRepo = txRepo;
    }

    @Transactional
    public Account createAccount(CreateAccountRequest req) {
        Account a = Account.builder()
                .ownerName(req.getOwnerName())
                .currency(req.getCurrency())
                .balance(BigDecimal.ZERO)
                .build();
        return repo.save(a);
    }

    @Transactional
    public void deleteAccount(Long id) {
        Account account = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found: " + id));
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Account balance must be zero to delete");
        }
        repo.delete(account);
    }


    @Transactional(readOnly = true)
    public Optional<Account> getAccount(Long id) {
        return repo.findById(id);
    }

    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BadRequestException("Amount must be positive");
        }
        Account acc = repo.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found: " + accountId));

        BigDecimal balanceBefore = acc.getBalance();
        BigDecimal balanceAfter = acc.getBalance().add(amount);
        acc.setBalance(balanceAfter);

        repo.save(acc);

        Transaction tx = Transaction.builder()
                .accountId(acc.getId())
                .balanceAfter(balanceAfter)
                .balanceBefore(balanceBefore)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .currency(acc.getCurrency())
                .correlationId(null)
                .build();

        txRepo.save(tx);

        return acc;
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts(){
        return repo.findAll();
    }

    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BadRequestException("Amount must be positive");
        }
        Account acc = repo.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found: " + accountId));

        BigDecimal before = acc.getBalance();
        if (before.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance for account " + acc.getOwnerName());
        }
        BigDecimal after = before.subtract(amount);
        acc.setBalance(after);
        repo.save(acc);

        // record transaction
        Transaction tx = Transaction.builder()
                .accountId(acc.getId())
                .type(TransactionType.WITHDRAWAL)
                .amount(amount)
                .currency(acc.getCurrency())
                .balanceBefore(before)
                .balanceAfter(after)
                .correlationId(null)
                .build();
        txRepo.save(tx);

        return acc;
    }

}