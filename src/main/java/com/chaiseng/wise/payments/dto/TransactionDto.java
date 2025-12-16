package com.chaiseng.wise.payments.dto;

import com.chaiseng.wise.payments.entity.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class TransactionDto {
    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private String currency;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private UUID correlationId;
    private OffsetDateTime createdAt;
}
