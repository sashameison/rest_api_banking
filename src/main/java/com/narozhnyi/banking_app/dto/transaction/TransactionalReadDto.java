package com.narozhnyi.banking_app.dto.transaction;

import java.math.BigDecimal;
import java.time.Instant;

import com.narozhnyi.banking_app.dto.account.AccountReadDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionalReadDto {

  private BigDecimal transferAmount;
  private AccountReadDto sender;
  private AccountReadDto receiver;
  private Instant createdAt;

}
