package com.chaiseng.wise.payments.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=32)
    private TransactionType type;

    @Column(nullable = false, precision=18, scale=2)
    private BigDecimal amount;

    @Column(nullable = false, length=3)
    private String currency;

    @Column(name="balance_before", nullable = false, precision=18, scale=2)
    private BigDecimal balanceBefore;

    @Column(name="balance_after", nullable = false, precision=18, scale=2)
    private BigDecimal balanceAfter;

    @Column(name = "correlation_id")
    private UUID correlationId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @PrePersist
    public void prePersist() {
        createdAt = OffsetDateTime.now();
    }
}
