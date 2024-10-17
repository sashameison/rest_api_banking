package com.narozhnyi.banking_app.dto.account;

import static com.narozhnyi.banking_app.util.Constants.Regex.ACCOUNT_NUMBER_REGEX;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreateDto {

  @NotBlank
  @Pattern(regexp = ACCOUNT_NUMBER_REGEX)
  private String accountNumber;

  @NotNull
  @Min(value = 0L)
  private BigDecimal balance;
}
