package com.narozhnyi.banking_app.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.math.BigDecimal;

import com.narozhnyi.banking_app.dto.transaction.DepositWithdrawFundDto;
import com.narozhnyi.banking_app.dto.transaction.TransactionalResponse;
import com.narozhnyi.banking_app.dto.transaction.TransferFundDto;
import com.narozhnyi.banking_app.entity.Account;
import com.narozhnyi.banking_app.entity.Transaction;
import com.narozhnyi.banking_app.mapper.TransactionalMapper;
import com.narozhnyi.banking_app.repository.AccountRepository;
import com.narozhnyi.banking_app.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

  public static final String ACCOUNT_NUMBER = "1234 5678 1234 5678";
  public static final String RECEIVER_ACCOUNT_NUMBER = "9876 5432 1098 7654";
  @Mock
  private TransactionRepository transactionRepository;
  @Mock
  private TransactionalMapper transactionalMapper;
  @Mock
  private AccountService accountService;
  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private TransactionService transactionService;

  @Test
  void shouldDepositSuccessfully() {
    DepositWithdrawFundDto depositDto = new DepositWithdrawFundDto(BigDecimal.valueOf(100), ACCOUNT_NUMBER);
    Account account = new Account();
    account.setAccountNumber(ACCOUNT_NUMBER);
    account.setBalance(BigDecimal.valueOf(1000));

    Transaction depositTransaction = new Transaction();
    when(accountService.depositAccountBalance(depositDto)).thenReturn(account);
    when(transactionalMapper.toDepositWithdrawTransaction(depositDto)).thenReturn(depositTransaction);
    when(transactionRepository.saveAndFlush(any(Transaction.class))).thenReturn(depositTransaction);
    when(transactionalMapper.toReadDto(depositTransaction)).thenReturn(new TransactionalResponse());

    TransactionalResponse result = transactionService.depositFund(depositDto);

    verify(accountService).depositAccountBalance(depositDto);
    verify(transactionRepository).saveAndFlush(depositTransaction);
    assertNotNull(result);
  }

  @Test
  void shouldThrowException_WhenAccountNotFound() {
    DepositWithdrawFundDto depositDto = new DepositWithdrawFundDto(BigDecimal.valueOf(100), ACCOUNT_NUMBER);
    when(accountService.depositAccountBalance(depositDto)).thenThrow(new ResponseStatusException(NOT_FOUND));

    assertThrows(ResponseStatusException.class, () -> transactionService.depositFund(depositDto));
  }

  @Test
  void shouldWithdrawSuccessfully() {
    DepositWithdrawFundDto withdrawDto = new DepositWithdrawFundDto(BigDecimal.valueOf(100), ACCOUNT_NUMBER);
    Account account = new Account();
    account.setAccountNumber(ACCOUNT_NUMBER);
    account.setBalance(BigDecimal.valueOf(1000));

    Transaction withdrawTransaction = new Transaction();
    when(accountService.withdrawAccountBalance(withdrawDto)).thenReturn(account);
    when(transactionalMapper.toDepositWithdrawTransaction(withdrawDto)).thenReturn(withdrawTransaction);
    when(transactionRepository.saveAndFlush(any(Transaction.class))).thenReturn(withdrawTransaction);
    when(transactionalMapper.toReadDto(withdrawTransaction)).thenReturn(new TransactionalResponse());

    TransactionalResponse result = transactionService.withdrawFunds(withdrawDto);

    verify(accountService).withdrawAccountBalance(withdrawDto);
    verify(transactionRepository).saveAndFlush(withdrawTransaction);
    assertNotNull(result);
  }

  @Test
  void shouldThrowExceptionWhenInsufficientBalance() {
    DepositWithdrawFundDto withdrawDto = new DepositWithdrawFundDto(BigDecimal.valueOf(100), ACCOUNT_NUMBER);
    when(accountService.withdrawAccountBalance(withdrawDto)).thenThrow(new ResponseStatusException(BAD_REQUEST));

    assertThrows(ResponseStatusException.class, () -> transactionService.withdrawFunds(withdrawDto));
  }

  @Test
  void shouldTransferSuccessfully() {
    TransferFundDto transferFundDto = new TransferFundDto(BigDecimal.valueOf(500), ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER);
    Account sender = new Account();
    sender.setAccountNumber(ACCOUNT_NUMBER);
    sender.setBalance(BigDecimal.valueOf(1000));

    Account receiver = new Account();
    receiver.setAccountNumber(RECEIVER_ACCOUNT_NUMBER);
    receiver.setBalance(BigDecimal.valueOf(500));

    Transaction transaction = new Transaction();
    when(accountService.withdrawAccountBalance(any(DepositWithdrawFundDto.class))).thenReturn(sender);
    when(accountService.depositAccountBalance(any(DepositWithdrawFundDto.class))).thenReturn(receiver);
    when(transactionalMapper.toTransaction(transferFundDto)).thenReturn(transaction);
    when(transactionRepository.saveAndFlush(transaction)).thenReturn(transaction);
    when(transactionalMapper.toReadDto(transaction)).thenReturn(new TransactionalResponse());

    TransactionalResponse result = transactionService.transferFunds(transferFundDto);

    verify(accountService).withdrawAccountBalance(any(DepositWithdrawFundDto.class));
    verify(accountService).depositAccountBalance(any(DepositWithdrawFundDto.class));
    verify(transactionRepository).saveAndFlush(transaction);
    assertNotNull(result);
  }

  @Test
  void shouldThrowExceptionWhenSenderHasInsufficientBalance() {
    TransferFundDto transferFundDto = new TransferFundDto(BigDecimal.valueOf(1000), ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER);
    when(accountService.withdrawAccountBalance(any(DepositWithdrawFundDto.class))).thenThrow(new ResponseStatusException(BAD_REQUEST));

    assertThrows(ResponseStatusException.class, () -> transactionService.transferFunds(transferFundDto));
  }
}
