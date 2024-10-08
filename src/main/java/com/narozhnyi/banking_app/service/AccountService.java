package com.narozhnyi.banking_app.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.math.BigDecimal;
import java.util.Optional;

import com.narozhnyi.banking_app.dto.account.AccountCreateDto;
import com.narozhnyi.banking_app.dto.account.AccountResponse;
import com.narozhnyi.banking_app.dto.transaction.DepositWithdrawFundDto;
import com.narozhnyi.banking_app.entity.Account;
import com.narozhnyi.banking_app.mapper.AccountMapper;
import com.narozhnyi.banking_app.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

  private final AccountRepository accountRepository;
  private final AccountMapper accountMapper;

  @Transactional
  public AccountResponse create(AccountCreateDto createEditDto) {
    return Optional.ofNullable(createEditDto)
        .map(accountMapper::toAccount)
        .map(accountRepository::save)
        .map(accountMapper::toReadDto)
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST));
  }

  public Account depositAccountBalance(DepositWithdrawFundDto depositWithdrawFundDto) {
    return accountRepository.findAccountByAccountNumber(depositWithdrawFundDto.getAccountNumber())
        .map(accountToUpdate -> {
          accountToUpdate.setBalance(accountToUpdate.getBalance().add(depositWithdrawFundDto.getTransferAmount()));
          return accountToUpdate;
        })
        .map(accountRepository::saveAndFlush)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
  }

  public Account withdrawAccountBalance(DepositWithdrawFundDto withdrawFund) {
    var account = accountRepository.findAccountByAccountNumber(withdrawFund.getAccountNumber())
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

    return Optional.of(account)
        .filter(senderAccount ->
            senderAccount.getBalance().subtract(withdrawFund.getTransferAmount()).compareTo(BigDecimal.ZERO) > 0)
        .map(accountToUpdate -> {
          accountToUpdate.setBalance(accountToUpdate.getBalance().subtract(withdrawFund.getTransferAmount()));
          return accountToUpdate;
        })
        .map(accountRepository::saveAndFlush)
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST));
  }

  public AccountResponse getByAccountNumber(String accountNumber) {
    return accountRepository.findAccountByAccountNumber(accountNumber)
        .map(accountMapper::toReadDto)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
  }

  public Page<AccountResponse> getAllBy(Pageable pageable) {
    return accountRepository.findAllBy(pageable)
        .map(accountMapper::toReadDto);
  }

}
