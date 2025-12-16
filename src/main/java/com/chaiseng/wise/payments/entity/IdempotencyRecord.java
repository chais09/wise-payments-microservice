package com.chaiseng.wise.payments.entity;

import jakarta.persistence.*;
import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.UniqueConstraint;

import java.time.OffsetDateTime;

@Entity
@Table(name="idempotency_keys",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"idempotency_key"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdempotencyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="idempotency_key", nullable=false, length=255)
    private String idempotencyKey;

    @Column(name="request_hash",nullable = false, length=255)
    private String requestHash;

    @Lob
    @Column(name="response_body", nullable=false, columnDefinition="text")
    private String responseBody;

    @Column(name="status_code", nullable = false)
    private int statusCode;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @PrePersist
    public void prePersist() {
        createdAt = OffsetDateTime.now();
    }
}
