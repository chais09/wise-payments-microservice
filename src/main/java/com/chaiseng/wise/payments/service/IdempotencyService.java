package com.chaiseng.wise.payments.service;

import com.chaiseng.wise.payments.entity.IdempotencyRecord;
import com.chaiseng.wise.payments.repository.IdempotencyRepository;
import com.chaiseng.wise.payments.exception.BadRequestException;
import com.chaiseng.wise.payments.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class IdempotencyService {

    private final IdempotencyRepository repo;
    private final ObjectMapper objectMapper;

    public IdempotencyService(IdempotencyRepository repo, ObjectMapper objectMapper) {
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    public String sha256(String input){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * If record exists and hash matches -> return stored record.
     * If exists and hash differs -> throw conflict
     * If not exists -> return empty
     */
    @Transactional(readOnly = true)
    public Optional<IdempotencyRecord> checkIdempotency(String idempotencyKey, Object requestBody) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return Optional.empty();
        }
        Optional<IdempotencyRecord> existing = repo.findByIdempotencyKey(idempotencyKey);
        if (existing.isEmpty()) return Optional.empty();

        String requestJson;
        try {
            requestJson = objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Cannot serialize request for idempotency check");
        }
        String hash = sha256(requestJson);
        IdempotencyRecord rec = existing.get();
        if (!rec.getRequestHash().equals(hash)) {
            throw new BadRequestException("Idempotency key already used with different payload");
        }
        return Optional.of(rec);
    }

    @Transactional
    public void storeIdempotencySuccess(String idempotencyKey, Object requestBody, Object responseBody, int status) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) return;
        String reqJson;
        String respJson;
        try {
            reqJson = objectMapper.writeValueAsString(requestBody);
            respJson = objectMapper.writeValueAsString(responseBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize idempotency request/response");
        }
        String hash = sha256(reqJson);
        IdempotencyRecord r = IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .requestHash(hash)
                .responseBody(respJson)
                .statusCode(status)
                .build();
        repo.save(r);
    }
}
