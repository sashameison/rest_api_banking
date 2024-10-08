package com.narozhnyi.banking_app.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Optional;

import com.narozhnyi.banking_app.entity.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class AccountRepositoryTest {

  @Autowired
  private AccountRepository accountRepository;

  private Account account;

  @BeforeEach
  void setUp() {
    account = new Account();
    account.setAccountNumber("1234 5678 1234 5678");
    account.setBalance(BigDecimal.valueOf(1000));
    account = accountRepository.save(account);
  }

  @Test
  void shouldReturnAccountWhenExists() {
    Optional<Account> foundAccount = accountRepository.findAccountByAccountNumber("1234 5678 1234 5678");

    assertTrue(foundAccount.isPresent());
    assertEquals(account.getAccountId(), foundAccount.get().getAccountId());
    assertEquals("1234 5678 1234 5678", foundAccount.get().getAccountNumber());
  }

  @Test
  void shouldReturnEmptyWhenDoesNotExist() {
    Optional<Account> foundAccount = accountRepository.findAccountByAccountNumber("1111 2222 3333 4444");

    assertFalse(foundAccount.isPresent());
  }

  @Test
  void shouldReturnPageOfAccounts() {
    Page<Account> accountsPage = accountRepository.findAllBy(PageRequest.of(0, 10));

    assertFalse(accountsPage.isEmpty());
    assertTrue(accountsPage.getContent().contains(account));
  }

  @Test
  void shouldPersistAccount() {
    Account newAccount = new Account();
    newAccount.setAccountNumber("9876 5432 1098 7654");
    newAccount.setBalance(BigDecimal.valueOf(500));

    Account savedAccount = accountRepository.save(newAccount);

    assertNotNull(savedAccount.getAccountId());
    assertEquals("9876 5432 1098 7654", savedAccount.getAccountNumber());
    assertEquals(BigDecimal.valueOf(500), savedAccount.getBalance());
  }
}