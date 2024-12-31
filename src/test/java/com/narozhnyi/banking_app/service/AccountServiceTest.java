package com.narozhnyi.banking_app.service;

import static java.time.Instant.now;

import static com.narozhnyi.banking_app.entity.TransactionType.DEPOSIT;
import static com.narozhnyi.banking_app.entity.TransactionType.WITHDRAW;
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

import com.narozhnyi.banking_app.dto.account.AccountCreateDto;
import com.narozhnyi.banking_app.dto.account.AccountDto;
import com.narozhnyi.banking_app.dto.account.AccountResponse;
import com.narozhnyi.banking_app.dto.transaction.PaymentDto;
import com.narozhnyi.banking_app.entity.Account;
import com.narozhnyi.banking_app.exception.AccountNotFoundException;
import com.narozhnyi.banking_app.exception.NotEnoughMoneyException;
import com.narozhnyi.banking_app.mapper.AccountMapper;
import com.narozhnyi.banking_app.repository.AccountRepository;
import com.narozhnyi.banking_app.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private AccountMapper accountMapper;

  @Mock
  private TransactionRepository transactionRepository;

  @InjectMocks
  private AccountService accountService;


  @Test
  void shouldCreateAccountSuccessfully() {
    AccountCreateDto createEditDto = new AccountCreateDto(ACCOUNT_NUMBER, BigDecimal.valueOf(1000));

    Account account = new Account();
    account.setAccountNumber(ACCOUNT_NUMBER);
    account.setBalance(BigDecimal.valueOf(1000));

    AccountResponse accountResponse = new AccountResponse(ACCOUNT_NUMBER, BigDecimal.valueOf(1000), now(), now());

    when(accountMapper.toAccount(createEditDto)).thenReturn(account);
    when(accountRepository.save(account)).thenReturn(account);
    when(accountMapper.toAccountResponse(account)).thenReturn(accountResponse);

    AccountResponse result = accountService.create(createEditDto);

    assertNotNull(result);
    assertEquals(accountResponse, result);
    verify(accountRepository).save(account);
    verify(transactionRepository).save(any());
  }

  @Test
  void shouldReturnAccountWhenAccountExists() {
    String accountNumber = ACCOUNT_NUMBER;
    Account account = new Account();
    account.setAccountNumber(accountNumber);
    account.setBalance(BigDecimal.valueOf(1000));

    AccountResponse accountResponse = new AccountResponse(ACCOUNT_NUMBER, BigDecimal.valueOf(1000), now(), now());

    when(accountRepository.findAccountByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
    when(accountMapper.toAccountResponse(account)).thenReturn(accountResponse);

    AccountResponse result = accountService.getByAccountNumber(accountNumber);

    assertNotNull(result);
    assertEquals(accountResponse, result);
    verify(accountRepository).findAccountByAccountNumber(accountNumber);
  }

  @Test
  void shouldThrowExceptionWhenAccountNotFound() {
    String accountNumber = ACCOUNT_NUMBER;

    when(accountRepository.findAccountByAccountNumber(accountNumber)).thenReturn(Optional.empty());

    assertThrows(AccountNotFoundException.class, () -> accountService.getByAccountNumber(accountNumber));
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
    AccountResponse accountResponse1 = new AccountResponse(ACCOUNT_NUMBER, BigDecimal.valueOf(1000), now(), now());
    AccountResponse accountResponse2 = new AccountResponse(RECEIVER_ACCOUNT_NUMBER, BigDecimal.valueOf(2000), now(), now());

    when(accountRepository.findAllBy(pageable)).thenReturn(accountPage);
    when(accountMapper.toAccountResponse(account1)).thenReturn(accountResponse1);
    when(accountMapper.toAccountResponse(account2)).thenReturn(accountResponse2);

    Page<AccountResponse> result = accountService.getAllBy(pageable);

    assertNotNull(result);
    assertEquals(2, result.getContent().size());
    verify(accountRepository).findAllBy(pageable);
  }

  @Test
  void shouldThrowExceptionNotFoundWhenGetByNotExistingAccountNumber() {
    when(accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER))
        .thenReturn(Optional.empty());

    var exception = assertThrows(AccountNotFoundException.class, () -> accountService.getByAccountNumber(ACCOUNT_NUMBER));

    assertThat(exception)
        .hasMessage("Account not found for account number: 1234 5678 1234 5678");
  }

  @Test
  void shouldReturnAccountByAccountNumber() {
    var accountCreateEditDto = new AccountCreateDto();
    accountCreateEditDto.setAccountNumber(ACCOUNT_NUMBER);

    var expectedAccountReadDto = new AccountResponse();
    expectedAccountReadDto.setAccountNumber(ACCOUNT_NUMBER);

    var account = new Account();
    account.setAccountNumber(ACCOUNT_NUMBER);

    when(accountRepository.findAccountByAccountNumber(ACCOUNT_NUMBER))
        .thenReturn(Optional.of(account));
    when(accountMapper.toAccountResponse(account)).thenReturn(expectedAccountReadDto);

    var actualAccountReadDto = accountService.getByAccountNumber(ACCOUNT_NUMBER);

    assertEquals(expectedAccountReadDto, actualAccountReadDto);
  }

  @Test
  void depositAccountBalance_successful() {
    PaymentDto depositDto = new PaymentDto(BigDecimal.valueOf(100), ACCOUNT_NUMBER, DEPOSIT);
    Account account = new Account();
    account.setAccountNumber("1234 5678 1234 5678");
    account.setBalance(BigDecimal.valueOf(200));

    AccountDto dto = new AccountDto();
    dto.setAccountNumber("1234 5678 1234 5678");
    dto.setBalance(BigDecimal.valueOf(300));

    when(accountRepository.findAccountByAccountNumber(depositDto.getAccountNumber())).thenReturn(Optional.of(account));
    when(accountRepository.save(any(Account.class))).thenReturn(account);
    when(accountMapper.toDto(account)).thenReturn(dto);

    AccountDto updatedAccount = accountService.depositAccountBalance(depositDto);

    assertNotNull(updatedAccount);
    assertEquals(BigDecimal.valueOf(300), updatedAccount.getBalance());
    verify(accountRepository).save(account);
  }

  @Test
  void depositAccountBalance_accountNotFound() {
    PaymentDto depositDto = new PaymentDto(BigDecimal.valueOf(100), ACCOUNT_NUMBER, DEPOSIT);

    when(accountRepository.findAccountByAccountNumber(depositDto.getAccountNumber())).thenReturn(Optional.empty());

    AccountNotFoundException exception = assertThrows(AccountNotFoundException.class,
        () -> accountService.depositAccountBalance(depositDto));

    assertEquals("Account not found for account number: 1234 5678 1234 5678", exception.getMessage());
    verify(accountRepository, never()).save(any(Account.class));
  }

  @Test
  void withdrawAccountBalance_successful() {
    PaymentDto withdrawDto = new PaymentDto(BigDecimal.valueOf(100), ACCOUNT_NUMBER, WITHDRAW);

    Account account = new Account();
    account.setAccountNumber(ACCOUNT_NUMBER);
    account.setBalance(BigDecimal.valueOf(200));

    AccountDto dto = new AccountDto();
    dto.setAccountNumber(ACCOUNT_NUMBER);
    dto.setBalance(BigDecimal.valueOf(100));

    when(accountRepository.findAccountByAccountNumber(withdrawDto.getAccountNumber())).thenReturn(Optional.of(account));
    when(accountRepository.save(any(Account.class))).thenReturn(account);
    when(accountMapper.toDto(account)).thenReturn(dto);

    AccountDto updatedAccount = accountService.withdrawAccountBalance(withdrawDto);

    assertNotNull(updatedAccount);
    assertEquals(BigDecimal.valueOf(100), updatedAccount.getBalance());
    verify(accountRepository).save(account);
  }

  @Test
  void withdrawAccountBalance_insufficientBalance() {
    PaymentDto withdrawDto = new PaymentDto(BigDecimal.valueOf(300), ACCOUNT_NUMBER, WITHDRAW);
    Account account = new Account();
    account.setAccountNumber("1234 5678 1234 5678");
    account.setBalance(BigDecimal.valueOf(200));

    when(accountRepository.findAccountByAccountNumber(withdrawDto.getAccountNumber())).thenReturn(Optional.of(account));

    NotEnoughMoneyException exception = assertThrows(NotEnoughMoneyException.class,
        () -> accountService.withdrawAccountBalance(withdrawDto));

    assertEquals("Not enough money to transfer.", exception.getMessage());
    verify(accountRepository, never()).save(any(Account.class));
  }

  @Test
  void withdrawAccountBalance_accountNotFound() {
    PaymentDto withdrawDto = new PaymentDto(BigDecimal.valueOf(100), ACCOUNT_NUMBER, WITHDRAW);

    when(accountRepository.findAccountByAccountNumber(withdrawDto.getAccountNumber())).thenReturn(Optional.empty());

    var exception = assertThrows(AccountNotFoundException.class,
        () -> accountService.withdrawAccountBalance(withdrawDto));

    assertEquals("Account not found for account number: 1234 5678 1234 5678", exception.getMessage());
    verify(accountRepository, never()).save(any(Account.class));
  }
}