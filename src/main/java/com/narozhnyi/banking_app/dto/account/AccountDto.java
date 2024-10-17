package com.narozhnyi.banking_app.dto.account;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class AccountDto {

  private UUID accountId;
  private String accountNumber;
  private BigDecimal balance;
  private Instant createdAt;
  private Instant modifiedAt;
}
