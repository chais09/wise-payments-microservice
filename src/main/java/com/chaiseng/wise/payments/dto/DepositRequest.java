package com.chaiseng.wise.payments.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequest {
    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message="Amount must be positive")
    private BigDecimal amount;
}