package com.narozhnyi.banking_app.service;

import static com.narozhnyi.banking_app.entity.TransactionType.DEPOSIT;
import static com.narozhnyi.banking_app.entity.TransactionType.WITHDRAW;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.math.BigDecimal;

import com.narozhnyi.banking_app.dto.account.AccountDto;
import com.narozhnyi.banking_app.dto.transaction.PaymentDto;
import com.narozhnyi.banking_app.dto.transaction.TransactionalResponse;
import com.narozhnyi.banking_app.dto.transaction.TransferFundDto;
import com.narozhnyi.banking_app.entity.Account;
import com.narozhnyi.banking_app.entity.Transaction;
import com.narozhnyi.banking_app.mapper.AccountMapper;
import com.narozhnyi.banking_app.mapper.TransactionalMapper;
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
  private AccountMapper accountMapper;

  @InjectMocks
  private TransactionService transactionService;

  @Test
  void shouldDepositSuccessfully() {
    PaymentDto depositDto = new PaymentDto(BigDecimal.valueOf(100), ACCOUNT_NUMBER, DEPOSIT);

    AccountDto dto = new AccountDto();
    dto.setAccountNumber(ACCOUNT_NUMBER);
    dto.setBalance(BigDecimal.valueOf(1000));

    Account account = new Account();
    account.setAccountNumber(ACCOUNT_NUMBER);
    account.setBalance(BigDecimal.valueOf(1000));

    Transaction depositTransaction = new Transaction();
    when(accountService.depositAccountBalance(depositDto)).thenReturn(dto);
    when(transactionalMapper.toAccountPaymentTransaction(depositDto)).thenReturn(depositTransaction);
    when(transactionRepository.save(any(Transaction.class))).thenReturn(depositTransaction);
    when(transactionalMapper.toReadDto(depositTransaction)).thenReturn(new TransactionalResponse());
    when(accountMapper.toAccountFromDto(dto)).thenReturn(account);

    TransactionalResponse result = transactionService.depositFund(depositDto);

    verify(accountService).depositAccountBalance(depositDto);
    verify(transactionRepository).save(depositTransaction);
    assertNotNull(result);
  }

  @Test
  void shouldThrowException_WhenAccountNotFound() {
    PaymentDto depositDto = new PaymentDto(BigDecimal.valueOf(100), ACCOUNT_NUMBER, DEPOSIT);
    when(accountService.depositAccountBalance(depositDto)).thenThrow(new ResponseStatusException(NOT_FOUND));

    assertThrows(ResponseStatusException.class, () -> transactionService.depositFund(depositDto));
  }

  @Test
  void shouldWithdrawSuccessfully() {
    var withdrawDto = new PaymentDto(BigDecimal.valueOf(100), ACCOUNT_NUMBER, WITHDRAW);
    AccountDto dto = new AccountDto();
    dto.setAccountNumber(ACCOUNT_NUMBER);
    dto.setBalance(BigDecimal.valueOf(1000));

    Transaction withdrawTransaction = new Transaction();
    when(accountService.withdrawAccountBalance(withdrawDto)).thenReturn(dto);
    when(transactionalMapper.toAccountPaymentTransaction(withdrawDto)).thenReturn(withdrawTransaction);
    when(transactionRepository.save(any(Transaction.class))).thenReturn(withdrawTransaction);
    when(transactionalMapper.toReadDto(withdrawTransaction)).thenReturn(new TransactionalResponse());
    when(accountMapper.toAccountFromDto(dto)).thenReturn(new Account());


    TransactionalResponse result = transactionService.withdrawFunds(withdrawDto);

    verify(accountService).withdrawAccountBalance(withdrawDto);
    verify(transactionRepository).save(withdrawTransaction);
    assertNotNull(result);
  }

  @Test
  void shouldThrowExceptionWhenInsufficientBalance() {
    var withdrawDto = new PaymentDto(BigDecimal.valueOf(100), ACCOUNT_NUMBER, WITHDRAW);
    when(accountService.withdrawAccountBalance(withdrawDto)).thenThrow(new ResponseStatusException(BAD_REQUEST));

    assertThrows(ResponseStatusException.class, () -> transactionService.withdrawFunds(withdrawDto));
  }

  @Test
  void shouldTransferSuccessfully() {
    var transferFundDto = new TransferFundDto(BigDecimal.valueOf(500), ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER);
    AccountDto sender = new AccountDto();
    sender.setAccountNumber(ACCOUNT_NUMBER);
    sender.setBalance(BigDecimal.valueOf(1000));

    AccountDto receiver = new AccountDto();
    receiver.setAccountNumber(RECEIVER_ACCOUNT_NUMBER);
    receiver.setBalance(BigDecimal.valueOf(500));

    Transaction transaction = new Transaction();
    when(accountService.withdrawAccountBalance(any(PaymentDto.class))).thenReturn(sender);
    when(accountService.depositAccountBalance(any(PaymentDto.class))).thenReturn(receiver);
    when(transactionalMapper.toTransaction(transferFundDto)).thenReturn(transaction);
    when(transactionalMapper.toDepositDto(transferFundDto)).thenReturn(new PaymentDto());
    when(transactionalMapper.toWithdrawDto(transferFundDto)).thenReturn(new PaymentDto());
    when(transactionRepository.save(transaction)).thenReturn(transaction);
    when(transactionalMapper.toReadDto(transaction)).thenReturn(new TransactionalResponse());

    TransactionalResponse result = transactionService.transferFunds(transferFundDto);

    verify(accountService).withdrawAccountBalance(any(PaymentDto.class));
    verify(accountService).depositAccountBalance(any(PaymentDto.class));
    verify(transactionRepository).save(transaction);
    assertNotNull(result);
  }

}
