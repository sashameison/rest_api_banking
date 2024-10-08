package com.narozhnyi.banking_app.service;

import static java.time.Instant.now;

import static com.narozhnyi.banking_app.service.TransactionServiceTest.ACCOUNT_NUMBER;
import static com.narozhnyi.banking_app.service.TransactionServiceTest.RECEIVER_ACCOUNT_NUMBER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.narozhnyi.banking_app.dto.account.AccountCreateEditDto;
import com.narozhnyi.banking_app.dto.account.AccountReadDto;
import com.narozhnyi.banking_app.dto.transaction.DepositWithdrawFundDto;
import com.narozhnyi.banking_app.entity.Account;
import com.narozhnyi.banking_app.mapper.AccountMapper;
import com.narozhnyi.banking_app.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private AccountMapper accountMapper;

  @InjectMocks
  private AccountService accountService;


  @Test
  void shouldCreateAccountSuccessfully() {
    AccountCreateEditDto createEditDto = new AccountCreateEditDto(ACCOUNT_NUMBER, BigDecimal.valueOf(1000));
    Account account = new Account();
    account.setAccountNumber(ACCOUNT_NUMBER);
    account.setBalance(BigDecimal.valueOf(1000));

    AccountReadDto accountReadDto = new AccountReadDto(ACCOUNT_NUMBER, BigDecimal.valueOf(1000), now(), now());

    when(accountMapper.toAccount(createEditDto)).thenReturn(account);
    when(accountRepository.save(account)).thenReturn(account);
    when(accountMapper.toReadDto(account)).thenReturn(accountReadDto);

    AccountReadDto result = accountService.create(createEditDto);

    assertNotNull(result);
    assertEquals(accountReadDto, result);
    verify(accountRepository).save(account);
  }

  @Test
  void shouldThrowExceptionWhenCreateEditDtoIsNull() {
    assertThrows(ResponseStatusException.class, () -> accountService.create(null));
  }

  @Test
  void shouldReturnAccountWhenAccountExists() {
    String accountNumber = ACCOUNT_NUMBER;
    Account account = new Account();
    account.setAccountNumber(accountNumber);
    account.setBalance(BigDecimal.valueOf(1000));

    AccountReadDto accountReadDto = new AccountReadDto(ACCOUNT_NUMBER, BigDecimal.valueOf(1000), now(), now());

    when(accountRepository.findAccountByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
    when(accountMapper.toReadDto(account)).thenReturn(accountReadDto);

    AccountReadDto result = accountService.getByAccountNumber(accountNumber);

    assertNotNull(result);
    assertEquals(accountReadDto, result);
    verify(accountRepository).findAccountByAccountNumber(accountNumber);
  }

  @Test
  void shouldThrowExceptionWhenAccountNotFound() {
    String accountNumber = ACCOUNT_NUMBER;

    when(accountRepository.findAccountByAccountNumber(accountNumber)).thenReturn(Optional.empty());

    assertThrows(ResponseStatusException.class, () -> accountService.getByAccountNumber(accountNumber));
  }

  @Test
  void shouldReturnPageOfAccounts() {
    PageRequest pageable = PageRequest.of(0, 10);
    Account account1 = new Account();
    account1.setAccountNumber(ACCOUNT_NUMBER);
    account1.setBalance(BigDecimal.valueOf(1000));

    Account account2 = new Account();
    account2.setAccountNumber(RECEIVER_ACCOUNT_NUMBER);
    account2.setBalance(BigDecimal.valueOf(2000));

    Page<Account> accountPage = new PageImpl<>(List.of(account1, account2));
    AccountReadDto accountReadDto1 = new AccountReadDto(ACCOUNT_NUMBER, BigDecimal.valueOf(1000), now(), now());
    AccountReadDto accountReadDto2 = new AccountReadDto(RECEIVER_ACCOUNT_NUMBER, BigDecimal.valueOf(2000), now(), now());

    when(accountRepository.findAllBy(pageable)).thenReturn(accountPage);
    when(accountMapper.toReadDto(account1)).thenReturn(accountReadDto1);
    when(accountMapper.toReadDto(account2)).thenReturn(accountReadDto2);

    Page<AccountReadDto> result = accountService.getAllBy(pageable);

    assertNotNull(result);
    assertEquals(2, result.getContent().size());
    verify(accountRepository).findAllBy(pageable);
  }

  @Test
  void shouldThrowVadRequest() {
    // Given
    AccountCreateEditDto nullInput = null;

    // When
    Exception exception = assertThrows(ResponseStatusException.class, () -> accountService.create(nullInput));

    // Then
    assertThat(exception)
        .hasMessage("400 BAD_REQUEST");
  }

  @Test
  void shouldThrowExceptionNotFoundWhenGetByNotExistingAccountNumber() {
    when(accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER))
        .thenReturn(Optional.empty());

    var exception = assertThrows(ResponseStatusException.class, () -> {
      accountService.getByAccountNumber(ACCOUNT_NUMBER);
    });

    assertThat(exception)
        .hasMessage("404 NOT_FOUND");
  }

  @Test
  void shouldReturnAccountByAccountNumber() {
    var accountCreateEditDto = new AccountCreateEditDto();
    accountCreateEditDto.setAccountNumber(ACCOUNT_NUMBER);

    var expectedAccountReadDto = new AccountReadDto();
    expectedAccountReadDto.setAccountNumber(ACCOUNT_NUMBER);

    var account = new Account();
    account.setAccountNumber(ACCOUNT_NUMBER);

    when(accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER))
        .thenReturn(Optional.of(account));
    when(accountMapper.toReadDto(account)).thenReturn(expectedAccountReadDto);

    var actualAccountReadDto = accountService.getByAccountNumber(ACCOUNT_NUMBER);

    assertEquals(expectedAccountReadDto, actualAccountReadDto);
  }

  @Test
  void depositAccountBalance_successful() {
    DepositWithdrawFundDto depositDto = new DepositWithdrawFundDto(BigDecimal.valueOf(100), "1234 5678 1234 5678");
    Account account = new Account();
    account.setAccountNumber("1234 5678 1234 5678");
    account.setBalance(BigDecimal.valueOf(200));

    when(accountRepository.findAccountByAccountNumber(depositDto.getAccountNumber())).thenReturn(Optional.of(account));
    when(accountRepository.saveAndFlush(any(Account.class))).thenReturn(account);

    Account updatedAccount = accountService.depositAccountBalance(depositDto);

    assertNotNull(updatedAccount);
    assertEquals(BigDecimal.valueOf(300), updatedAccount.getBalance());
    verify(accountRepository).saveAndFlush(account);
  }

  @Test
  void depositAccountBalance_accountNotFound() {
    DepositWithdrawFundDto depositDto = new DepositWithdrawFundDto(BigDecimal.valueOf(100), "1234 5678 1234 5678");

    when(accountRepository.findAccountByAccountNumber(depositDto.getAccountNumber())).thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> accountService.depositAccountBalance(depositDto));

    assertEquals("404 NOT_FOUND", exception.getMessage());
    verify(accountRepository, never()).saveAndFlush(any(Account.class));
  }

  @Test
  void withdrawAccountBalance_successful() {
    DepositWithdrawFundDto withdrawDto = new DepositWithdrawFundDto(BigDecimal.valueOf(100), "1234 5678 1234 5678");
    Account account = new Account();
    account.setAccountNumber(ACCOUNT_NUMBER);
    account.setBalance(BigDecimal.valueOf(200));

    when(accountRepository.findAccountByAccountNumber(withdrawDto.getAccountNumber())).thenReturn(Optional.of(account));
    when(accountRepository.saveAndFlush(any(Account.class))).thenReturn(account);

    Account updatedAccount = accountService.withdrawAccountBalance(withdrawDto);

    assertNotNull(updatedAccount);
    assertEquals(BigDecimal.valueOf(100), updatedAccount.getBalance());
    verify(accountRepository).saveAndFlush(account);
  }

  @Test
  void withdrawAccountBalance_insufficientBalance() {
    DepositWithdrawFundDto withdrawDto = new DepositWithdrawFundDto(BigDecimal.valueOf(300), "1234 5678 1234 5678");
    Account account = new Account();
    account.setAccountNumber("1234 5678 1234 5678");
    account.setBalance(BigDecimal.valueOf(200));

    when(accountRepository.findAccountByAccountNumber(withdrawDto.getAccountNumber())).thenReturn(Optional.of(account));

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> accountService.withdrawAccountBalance(withdrawDto));

    assertEquals("400 BAD_REQUEST", exception.getMessage());
    verify(accountRepository, never()).saveAndFlush(any(Account.class));
  }

  @Test
  void withdrawAccountBalance_accountNotFound() {
    var withdrawDto = new DepositWithdrawFundDto(BigDecimal.valueOf(100), ACCOUNT_NUMBER);

    when(accountRepository.findAccountByAccountNumber(withdrawDto.getAccountNumber())).thenReturn(Optional.empty());

    var exception = assertThrows(ResponseStatusException.class,
        () -> accountService.withdrawAccountBalance(withdrawDto));

    assertEquals("404 NOT_FOUND", exception.getMessage());
    verify(accountRepository, never()).saveAndFlush(any(Account.class));
  }
}