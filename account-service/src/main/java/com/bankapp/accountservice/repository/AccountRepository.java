package com.bankapp.accountservice.repository;

import com.bankapp.accountservice.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountNumber(String accountNumber);

    /** A customer can own more than one account, so this is a List, not an Optional. */
    List<Account> findByUsername(String username);

    /**
     * Locks the row for the duration of the current transaction, so two concurrent
     * deposit/withdraw requests against the SAME account cannot both read the
     * balance, both compute a new value, and both write it back - the classic
     * "lost update" race condition. The second request simply waits for the first
     * transaction to commit (or roll back) before it can even read the row.
     *
     * Use this - not findById - anywhere a balance is about to be read and then
     * changed. Plain reads (GET endpoints) don't need it.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") Long id);
}
