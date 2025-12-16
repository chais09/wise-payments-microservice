package com.chaiseng.wise.payments.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class TransferResponse {
    private UUID transferId;          // correlationId
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private String currency;
    private String status;            // COMPLETED
    private OffsetDateTime createdAt;
}
