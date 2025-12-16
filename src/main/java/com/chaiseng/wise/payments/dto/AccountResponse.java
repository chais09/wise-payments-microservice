package com.chaiseng.wise.payments.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class AccountResponse {
    private Long id;
    private String ownerName;
    private String currency;
    private BigDecimal balance;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
