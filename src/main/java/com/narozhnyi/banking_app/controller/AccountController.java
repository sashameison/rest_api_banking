package com.narozhnyi.banking_app.controller;

import com.narozhnyi.banking_app.dto.account.AccountCreateEditDto;
import com.narozhnyi.banking_app.dto.account.AccountReadDto;
import com.narozhnyi.banking_app.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("api/v1/accounts")
public class AccountController {

  private final AccountService accountService;

  @GetMapping
  public Page<AccountReadDto> getAllBy(@PageableDefault Pageable pageable) {
    return accountService.getAllBy(pageable);
  }

  @GetMapping("/{account-number}")
  public AccountReadDto getByAccountNumber(@PathVariable("account-number") String accountNumber) {
    return accountService.getByAccountNumber(accountNumber);
  }

  @PostMapping
  public AccountReadDto create(@Valid @RequestBody AccountCreateEditDto accountCreateEditDto) {
    return accountService.create(accountCreateEditDto);
  }

}
