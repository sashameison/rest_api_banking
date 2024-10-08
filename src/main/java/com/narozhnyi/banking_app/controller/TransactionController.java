package com.narozhnyi.banking_app.controller;

import com.narozhnyi.banking_app.dto.transaction.DepositWithdrawFundDto;
import com.narozhnyi.banking_app.dto.transaction.TransactionalReadDto;
import com.narozhnyi.banking_app.dto.transaction.TransferFundDto;
import com.narozhnyi.banking_app.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("api/v1/transactions")
public class TransactionController {

  private final TransactionService transactionService;

  @PostMapping
  public TransactionalReadDto create(@NotNull @Valid @RequestBody TransferFundDto transferFundDto) {
    return transactionService.transferFunds(transferFundDto);
  }

  @PostMapping("/deposit")
  public TransactionalReadDto deposit(@NotNull @Valid @RequestBody DepositWithdrawFundDto depositFundDto) {
    return transactionService.depositFund(depositFundDto);
  }

  @PostMapping("/withdraw")
  public TransactionalReadDto withdraw(@NotNull @Valid @RequestBody DepositWithdrawFundDto withdrawFundDto) {
    return transactionService.withdrawFunds(withdrawFundDto);
  }
}
