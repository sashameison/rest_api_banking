package com.narozhnyi.banking_app.service;

import static java.lang.String.format;

import static com.narozhnyi.banking_app.entity.TransactionType.DEPOSIT;
import static com.narozhnyi.banking_app.util.Constants.Errors.ACCOUNT_NOT_FOUND;
import static com.narozhnyi.banking_app.util.Constants.Errors.NOT_ENOUGH_MONEY;

import java.math.BigDecimal;

import com.narozhnyi.banking_app.dto.account.AccountCreateDto;
import com.narozhnyi.banking_app.dto.account.AccountDto;
import com.narozhnyi.banking_app.dto.account.AccountResponse;
import com.narozhnyi.banking_app.dto.transaction.DepositWithdrawFundDto;
import com.narozhnyi.banking_app.entity.Account;
import com.narozhnyi.banking_app.entity.Transaction;
import com.narozhnyi.banking_app.exception.AccountNotFound;
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
    var account = accountMapper.toAccount(createEditDto);
    var accountToSave = accountRepository.save(account);

    transactionRepository.save(Transaction.builder()
            .transactionType(DEPOSIT)
            .receiver(accountToSave)
            .transferAmount(createEditDto.getBalance())
        .build());

    return accountMapper.toAccountResponse(accountToSave);
  }

  @Transactional
  public AccountDto depositAccountBalance(DepositWithdrawFundDto depositWithdrawFundDto) {
    var accountNumber = depositWithdrawFundDto.getAccountNumber();
    var transferAmount = depositWithdrawFundDto.getTransferAmount();

    var account = accountRepository.findAccountByAccountNumber(accountNumber)
        .orElseThrow(() -> new AccountNotFound(format(ACCOUNT_NOT_FOUND, accountNumber)));

    account.depositBalance(transferAmount);

    return accountMapper.toDto(accountRepository.save(account));
  }

  @Transactional
  public AccountDto withdrawAccountBalance(DepositWithdrawFundDto withdrawFund) {
    var accountNumber = withdrawFund.getAccountNumber();
    var transferAmount = withdrawFund.getTransferAmount();

    var account = accountRepository.findAccountByAccountNumber(accountNumber)
        .orElseThrow(() -> new AccountNotFound(format(ACCOUNT_NOT_FOUND, accountNumber)));

    validateSufficientFunds(account, transferAmount);

    account.withdrawBalance(transferAmount);

    return accountMapper.toDto(accountRepository.save(account));
  }

  public AccountResponse getByAccountNumber(String accountNumber) {
    return accountRepository.findAccountByAccountNumber(accountNumber)
        .map(accountMapper::toAccountResponse)
        .orElseThrow(() -> new AccountNotFound(format(ACCOUNT_NOT_FOUND, accountNumber)));
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
