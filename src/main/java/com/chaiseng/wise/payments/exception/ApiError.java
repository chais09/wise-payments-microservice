package com.chaiseng.wise.payments.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class ApiError {
    private String error;
    private String message;
    private OffsetDateTime timestamp;
}
