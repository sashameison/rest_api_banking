package com.narozhnyi.banking_app.dto.transaction;

import static com.narozhnyi.banking_app.util.Constants.Regex.ACCOUNT_NUMBER_REGEX;

import java.math.BigDecimal;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositWithdrawFundDto {

  @Positive
  private BigDecimal transferAmount;

  @Pattern(regexp = ACCOUNT_NUMBER_REGEX)
  private String accountNumber;
}
