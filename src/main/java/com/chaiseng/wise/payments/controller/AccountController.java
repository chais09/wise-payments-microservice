package com.chaiseng.wise.payments.controller;

import com.chaiseng.wise.payments.dto.*;
import com.chaiseng.wise.payments.entity.Account;
import com.chaiseng.wise.payments.exception.BadRequestException;
import com.chaiseng.wise.payments.exception.NotFoundException;
import com.chaiseng.wise.payments.mapper.AccountMapper;
import com.chaiseng.wise.payments.mapper.TransactionMapper;
import com.chaiseng.wise.payments.repository.TransactionRepository;
import com.chaiseng.wise.payments.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController{

    private final AccountService service;
    private final TransactionRepository txRepo;

    public AccountController(AccountService service, TransactionRepository txRepo) {
        this.service = service;
        this.txRepo = txRepo;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest  req) {
        Account created  = service.createAccount(req);
        AccountResponse res = AccountMapper.toDto(created);
        return ResponseEntity.created(URI.create("/api/v1/accounts/" + created.getId())).body(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        service.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        return service.getAccount(id)
                .map(AccountMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Account not found: "+ id));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccount() {
        List<AccountResponse> accounts = service.getAllAccounts()
                .stream()
                .map(AccountMapper::toDto)
                .toList();
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponse> deposit(@PathVariable Long id, @Valid @RequestBody DepositRequest req) {
        Account updated = service.deposit(id, req.getAmount()); // may throw NotFound or BadRequest or IllegalArgumentException
        return ResponseEntity.ok(AccountMapper.toDto(updated));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@PathVariable Long id, @Valid @RequestBody WithdrawRequest req) {
        Account updated = service.withdraw(id, req.getAmount()); // may throw InsufficientFundsException
        return ResponseEntity.ok(AccountMapper.toDto(updated));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<?> getTransactions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // ensure account exists
        service.getAccount(id).orElseThrow(() -> new NotFoundException("Account not found: " + id));

        Pageable p = PageRequest.of(page, size);
        Page<TransactionDto> txPage = txRepo.findByAccountIdOrderByCreatedAtDesc(id, p)
                .map(TransactionMapper::toDto);
        return ResponseEntity.ok(txPage);
    }
}
