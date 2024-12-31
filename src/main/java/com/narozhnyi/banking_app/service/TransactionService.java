package com.narozhnyi.banking_app.service;

import com.narozhnyi.banking_app.dto.transaction.PaymentDto;
import com.narozhnyi.banking_app.dto.transaction.TransactionalResponse;
import com.narozhnyi.banking_app.dto.transaction.TransferFundDto;
import com.narozhnyi.banking_app.mapper.AccountMapper;
import com.narozhnyi.banking_app.mapper.TransactionalMapper;
import com.narozhnyi.banking_app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final TransactionalMapper transactionalMapper;
  private final AccountService accountService;
  private final AccountMapper accountMapper;

  public TransactionalResponse depositFund(PaymentDto depositFundDto) {
    var transaction = transactionalMapper.toAccountPaymentTransaction(depositFundDto);

    var receiver = accountService.depositAccountBalance(depositFundDto);
    transaction.setReceiver(accountMapper.toAccountFromDto(receiver));

    transactionRepository.save(transaction);
    return transactionalMapper.toReadDto(transaction);
  }

  public TransactionalResponse withdrawFunds(PaymentDto withdrawFund) {
    var transaction = transactionalMapper.toAccountPaymentTransaction(withdrawFund);

    var receiver = accountService.withdrawAccountBalance(withdrawFund);
    transaction.setReceiver(accountMapper.toAccountFromDto(receiver));

    transactionRepository.save(transaction);
    return transactionalMapper.toReadDto(transaction);
  }

  public TransactionalResponse transferFunds(TransferFundDto transferFundDto) {
    var transaction = transactionalMapper.toTransaction(transferFundDto);

    var sender = accountService.depositAccountBalance(transactionalMapper.toDepositDto(transferFundDto));

    var receiver = accountService.withdrawAccountBalance(transactionalMapper.toWithdrawDto(transferFundDto));

    transaction.setSender(accountMapper.toAccountFromDto(sender));
    transaction.setReceiver(accountMapper.toAccountFromDto(receiver));

    transactionRepository.save(transaction);

    return transactionalMapper.toReadDto(transaction);
  }
}
