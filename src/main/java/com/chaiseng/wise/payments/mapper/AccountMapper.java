package com.chaiseng.wise.payments.mapper;

import com.chaiseng.wise.payments.dto.AccountResponse;
import com.chaiseng.wise.payments.entity.Account;

public final class AccountMapper {
    private AccountMapper() {}

    public static AccountResponse toDto(Account a) {
        if (a == null) return null;
        AccountResponse r = new AccountResponse();
        r.setId(a.getId());
        r.setOwnerName(a.getOwnerName());
        r.setCurrency(a.getCurrency());
        r.setBalance(a.getBalance());
        r.setCreatedAt(a.getCreatedAt());
        r.setUpdatedAt(a.getUpdatedAt());
        return r;
    }
}