package com.chaiseng.wise.payments.mapper;

import com.chaiseng.wise.payments.dto.TransactionDto;
import com.chaiseng.wise.payments.entity.Transaction;

public final class TransactionMapper {
    private TransactionMapper() {}

    public static TransactionDto toDto(Transaction t) {
        if (t == null) return null;
        TransactionDto d = new TransactionDto();
        d.setId(t.getId());
        d.setType(t.getType());
        d.setAmount(t.getAmount());
        d.setCurrency(t.getCurrency());
        d.setBalanceBefore(t.getBalanceBefore());
        d.setBalanceAfter(t.getBalanceAfter());
        d.setCorrelationId(t.getCorrelationId());
        d.setCreatedAt(t.getCreatedAt());

        return d;
    }
}
