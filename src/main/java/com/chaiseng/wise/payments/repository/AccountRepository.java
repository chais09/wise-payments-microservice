package com.chaiseng.wise.payments.repository;

import com.chaiseng.wise.payments.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // You can add custom queries later, e.g. findByOwnerName(...)
}