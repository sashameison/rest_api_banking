package com.narozhnyi.banking_app.dto.transaction;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.narozhnyi.banking_app.dto.account.AccountResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class TransactionalResponse {

  private BigDecimal transferAmount;
  private AccountResponse sender;
  private AccountResponse receiver;
  private Instant createdAt;

}
