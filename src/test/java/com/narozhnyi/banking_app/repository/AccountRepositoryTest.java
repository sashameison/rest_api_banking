package com.narozhnyi.banking_app.repository;

import static com.narozhnyi.banking_app.service.TransactionServiceTest.ACCOUNT_NUMBER;
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
    account.setAccountNumber(ACCOUNT_NUMBER);
    account.setBalance(BigDecimal.valueOf(1000));
    account = accountRepository.save(account);
  }

  @Test
  void shouldReturnAccountWhenExists() {
    Optional<Account> foundAccount = accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER);

    assertTrue(foundAccount.isPresent());
    assertEquals(account.getAccountId(), foundAccount.get().getAccountId());
    assertEquals(ACCOUNT_NUMBER, foundAccount.get().getAccountNumber());
  }

  @Test
  void shouldReturnEmptyWhenDoesNotExist() {
    Optional<Account> foundAccount = accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER);

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
    newAccount.setAccountNumber(ACCOUNT_NUMBER);
    newAccount.setBalance(BigDecimal.valueOf(500));

    Account savedAccount = accountRepository.save(newAccount);

    assertNotNull(savedAccount.getAccountId());
    assertEquals(ACCOUNT_NUMBER, savedAccount.getAccountNumber());
    assertEquals(BigDecimal.valueOf(500), savedAccount.getBalance());
  }
}