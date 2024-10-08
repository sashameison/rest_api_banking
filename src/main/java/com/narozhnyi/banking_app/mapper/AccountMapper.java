package com.narozhnyi.banking_app.mapper;

import com.narozhnyi.banking_app.dto.account.AccountCreateEditDto;
import com.narozhnyi.banking_app.dto.account.AccountReadDto;
import com.narozhnyi.banking_app.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

  Account toAccount(AccountCreateEditDto createEditDto);

  AccountReadDto toReadDto(Account account);


}
