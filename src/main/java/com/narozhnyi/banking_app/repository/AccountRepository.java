package com.narozhnyi.banking_app.repository;

import java.util.Optional;
import java.util.UUID;

import com.narozhnyi.banking_app.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

  Optional<Account> findAccountByAccountNumber(String accountNumber);

  Page<Account> findAllBy(Pageable pageable);
}
