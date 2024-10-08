package com.narozhnyi.banking_app.dto.account;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountReadDto {

  private String accountNumber;
  private BigDecimal balance;
  private Instant createdAt;
  private Instant modifiedAt;
}
