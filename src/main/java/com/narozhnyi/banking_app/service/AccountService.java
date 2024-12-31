package com.narozhnyi.banking_app.service;

import static java.lang.String.format;

import static com.narozhnyi.banking_app.entity.TransactionType.DEPOSIT;
import static com.narozhnyi.banking_app.util.Constants.Errors.ACCOUNT_NOT_FOUND_ERROR;
import static com.narozhnyi.banking_app.util.Constants.Errors.NOT_ENOUGH_MONEY;

import java.math.BigDecimal;

import com.narozhnyi.banking_app.dto.account.AccountCreateDto;
import com.narozhnyi.banking_app.dto.account.AccountDto;
import com.narozhnyi.banking_app.dto.account.AccountResponse;
import com.narozhnyi.banking_app.dto.transaction.PaymentDto;
import com.narozhnyi.banking_app.entity.Account;
import com.narozhnyi.banking_app.entity.Transaction;
import com.narozhnyi.banking_app.exception.AccountNotFoundException;
import com.narozhnyi.banking_app.exception.NotEnoughMoneyException;
import com.narozhnyi.banking_app.mapper.AccountMapper;
import com.narozhnyi.banking_app.repository.AccountRepository;
import com.narozhnyi.banking_app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

  private final AccountRepository accountRepository;
  private final AccountMapper accountMapper;
  private final TransactionRepository transactionRepository;

  @Transactional
  public AccountResponse create(AccountCreateDto createEditDto) {
    var accountToSave = accountMapper.toAccount(createEditDto);
    var accountEntity = accountRepository.save(accountToSave);

    if (createEditDto.getBalance().compareTo(BigDecimal.ZERO) > 0) {
      transactionRepository.save(Transaction.builder()
          .transactionType(DEPOSIT)
          .receiver(accountEntity)
          .transferAmount(createEditDto.getBalance())
          .build());
    }

    return accountMapper.toAccountResponse(accountEntity);
  }

  @Transactional
  public AccountDto depositAccountBalance(PaymentDto depositFundDto) {
    var accountNumber = depositFundDto.getAccountNumber();
    var transferAmount = depositFundDto.getTransferAmount();

    var accountEntity = accountRepository.findAccountByAccountNumber(accountNumber)
        .orElseThrow(() -> new AccountNotFoundException(format(ACCOUNT_NOT_FOUND_ERROR, accountNumber)));

    accountEntity.depositBalance(transferAmount);

    return accountMapper.toDto(accountRepository.save(accountEntity));
  }

  @Transactional
  public AccountDto withdrawAccountBalance(PaymentDto withdrawFund) {
    var accountNumber = withdrawFund.getAccountNumber();
    var transferAmount = withdrawFund.getTransferAmount();

    var accountEntity = accountRepository.findAccountByAccountNumber(accountNumber)
        .orElseThrow(() -> new AccountNotFoundException(format(ACCOUNT_NOT_FOUND_ERROR, accountNumber)));

    validateSufficientFunds(accountEntity, transferAmount);

    accountEntity.withdrawBalance(transferAmount);

    return accountMapper.toDto(accountRepository.save(accountEntity));
  }

  public AccountResponse getByAccountNumber(String accountNumber) {
    return accountRepository.findAccountByAccountNumber(accountNumber)
        .map(accountMapper::toAccountResponse)
        .orElseThrow(() -> new AccountNotFoundException(format(ACCOUNT_NOT_FOUND_ERROR, accountNumber)));
  }

  public Page<AccountResponse> getAllBy(Pageable pageable) {
    return accountRepository.findAllBy(pageable)
        .map(accountMapper::toAccountResponse);
  }

  private void validateSufficientFunds(Account account, BigDecimal amount) {
    if (account.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
      throw new NotEnoughMoneyException(NOT_ENOUGH_MONEY);
    }
  }
}
