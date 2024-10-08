package com.narozhnyi.banking_app.mapper;

import com.narozhnyi.banking_app.dto.account.AccountCreateDto;
import com.narozhnyi.banking_app.dto.account.AccountResponse;
import com.narozhnyi.banking_app.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

  Account toAccount(AccountCreateDto createEditDto);

  AccountResponse toReadDto(Account account);


}
