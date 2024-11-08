package com.narozhnyi.banking_app.service;

import static com.narozhnyi.banking_app.entity.TransactionType.TRANSFER;

import com.narozhnyi.banking_app.dto.transaction.DepositWithdrawFundDto;
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

  public TransactionalResponse depositFund(DepositWithdrawFundDto depositWithdrawFundDto) {
    var transaction = transactionalMapper.toDepositWithdrawTransaction(depositWithdrawFundDto);

    var receiver = accountService.depositAccountBalance(depositWithdrawFundDto);
    transaction.setReceiver(accountMapper.toAccountFromDto(receiver));

    transactionRepository.save(transaction);
    return transactionalMapper.toReadDto(transaction);
  }

  public TransactionalResponse withdrawFunds(DepositWithdrawFundDto withdrawFund) {
    var transaction = transactionalMapper.toDepositWithdrawTransaction(withdrawFund);

    var receiver = accountService.withdrawAccountBalance(withdrawFund);
    transaction.setReceiver(accountMapper.toAccountFromDto(receiver));

    transactionRepository.save(transaction);
    return transactionalMapper.toReadDto(transaction);
  }

  public TransactionalResponse transferFunds(TransferFundDto transferFundDto) {
    var transaction = transactionalMapper.toTransaction(transferFundDto);

    var sender = accountService.depositAccountBalance(
        DepositWithdrawFundDto.builder()
            .accountNumber(transferFundDto.getSenderAccountNumber())
            .transferAmount(transferFundDto.getTransferAmount())
            .transactionType(TRANSFER)
            .build());

    var receiver = accountService.withdrawAccountBalance(
        DepositWithdrawFundDto.builder()
            .accountNumber(transferFundDto.getReceiverAccountNumber())
            .transferAmount(transferFundDto.getTransferAmount())
            .transactionType(TRANSFER)
            .build());

    transaction.setSender(accountMapper.toAccountFromDto(sender));
    transaction.setReceiver(accountMapper.toAccountFromDto(receiver));

    transactionRepository.save(transaction);

    return transactionalMapper.toReadDto(transaction);
  }
}
