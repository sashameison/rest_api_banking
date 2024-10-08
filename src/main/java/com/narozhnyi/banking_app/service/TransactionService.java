package com.narozhnyi.banking_app.service;

import java.util.List;

import com.narozhnyi.banking_app.dto.transaction.DepositWithdrawFundDto;
import com.narozhnyi.banking_app.dto.transaction.TransactionalReadDto;
import com.narozhnyi.banking_app.dto.transaction.TransferFundDto;
import com.narozhnyi.banking_app.mapper.TransactionalMapper;
import com.narozhnyi.banking_app.repository.AccountRepository;
import com.narozhnyi.banking_app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final TransactionalMapper transactionalMapper;
  private final AccountService accountService;
  private final AccountRepository accountRepository;

  @Transactional
  public TransactionalReadDto depositFund(DepositWithdrawFundDto depositWithdrawFundDto) {
    var depositTransaction = transactionalMapper.toDepositWithdrawTransaction(depositWithdrawFundDto);

    var receiver = accountService.depositAccountBalance(depositWithdrawFundDto);
    depositTransaction.setReceiver(receiver);

    transactionRepository.saveAndFlush(depositTransaction);
    return transactionalMapper.toReadDto(depositTransaction);
  }

  @Transactional
  public TransactionalReadDto withdrawFunds(DepositWithdrawFundDto withdrawFund) {
    var withdrawTransaction = transactionalMapper.toDepositWithdrawTransaction(withdrawFund);

    var receiver = accountService.withdrawAccountBalance(withdrawFund);
    withdrawTransaction.setReceiver(receiver);

    transactionRepository.saveAndFlush(withdrawTransaction);
    return transactionalMapper.toReadDto(withdrawTransaction);
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public TransactionalReadDto transferFunds(TransferFundDto transferFundDto) {
    var transaction = transactionalMapper.toTransaction(transferFundDto);

    var sender = accountService.depositAccountBalance(new DepositWithdrawFundDto(
        transferFundDto.getTransferAmount(),
        transferFundDto.getSenderAccountNumber()));

    var receiver = accountService.withdrawAccountBalance(new DepositWithdrawFundDto(
        transferFundDto.getTransferAmount(),
        transferFundDto.getReceiverAccountNumber()));

    accountRepository.saveAllAndFlush(List.of(sender, receiver));

    transaction.setSender(sender);
    transaction.setReceiver(receiver);

    transactionRepository.saveAndFlush(transaction);

    return transactionalMapper.toReadDto(transaction);
  }
}
