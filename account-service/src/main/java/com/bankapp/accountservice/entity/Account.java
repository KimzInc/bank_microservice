package com.bankapp.accountservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A bank account. "username" is the owner - the link back to auth-service's
 * User, same pattern used throughout this project (JWT subject, never a
 * cross-database foreign key). One customer can own several accounts (e.g.
 * one SAVINGS, one CHECKING), so unlike Customer/Employee this is NOT
 * one-row-per-username - see AccountRepository.findByUsername returning a List.
 *
 * balance is BigDecimal, never float/double - binary floating point cannot
 * represent most decimal currency amounts exactly, and that rounding error
 * compounds with every transaction. This is a real rule for money in any
 * language, not specific to this project.
 */
@Entity
@Table(name = "accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = "account_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;

    @Column(nullable = false, length = 50)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
