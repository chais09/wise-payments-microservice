package com.chaiseng.wise.payments.controller;

import com.chaiseng.wise.payments.dto.TransferRequest;
import com.chaiseng.wise.payments.dto.TransferResponse;
import com.chaiseng.wise.payments.entity.IdempotencyRecord;
import com.chaiseng.wise.payments.exception.NotFoundException;
import com.chaiseng.wise.payments.service.IdempotencyService;
import com.chaiseng.wise.payments.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;
    private final IdempotencyService idempotencyService;

    public TransferController(TransferService transferService, IdempotencyService idempotencyService) {
        this.transferService = transferService;
        this.idempotencyService = idempotencyService;
    }

    @PostMapping
    public ResponseEntity<Object> createTransfer( @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey, @Valid @RequestBody TransferRequest req) {
        TransferResponse res = this.transferService.transfer(req);
        // 1) check existing idempotency
        Optional<IdempotencyRecord> existing = idempotencyService.checkIdempotency(idempotencyKey, req);
        if (existing.isPresent()) {
            IdempotencyRecord r = existing.get();
            // return stored response JSON as-is with the stored status code
            return ResponseEntity.status(r.getStatusCode()).body(r.getResponseBody());
        }

        // 2) process transfer
        TransferResponse resp = transferService.transfer(req);

        // 3) store idempotency success (store the response)
        idempotencyService.storeIdempotencySuccess(idempotencyKey, req, resp, HttpStatus.OK.value());

        return ResponseEntity.ok(resp);
    }
}
