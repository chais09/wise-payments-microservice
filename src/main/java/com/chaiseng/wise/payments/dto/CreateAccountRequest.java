package com.chaiseng.wise.payments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @NotBlank(message = "ownerName is required")
    private String ownerName;

    @NotBlank(message = "currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3-letter code like USD")
    private String currency;
}
