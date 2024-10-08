package com.narozhnyi.banking_app.validation;

import com.narozhnyi.banking_app.dto.account.AccountCreateEditDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static com.narozhnyi.banking_app.service.TransactionServiceTest.ACCOUNT_NUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountCreateEditDtoValidationTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldBeValidAccountCreateEditDto() {
    AccountCreateEditDto dto = new AccountCreateEditDto(ACCOUNT_NUMBER, BigDecimal.valueOf(100.00));

    Set<ConstraintViolation<AccountCreateEditDto>> violations = validator.validate(dto);

    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldFailValidationWhenAccountNumberIsNull() {
    AccountCreateEditDto dto = new AccountCreateEditDto(null, BigDecimal.valueOf(100.00));

    Set<ConstraintViolation<AccountCreateEditDto>> violations = validator.validate(dto);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());

    ConstraintViolation<AccountCreateEditDto> violation = violations.iterator().next();
    assertEquals("must not be null", violation.getMessage());
    assertEquals("accountNumber", violation.getPropertyPath().toString());
  }

  @Test
  void shouldFailValidationWhenAccountNumberDoesNotMatchPattern() {
    AccountCreateEditDto dto = new AccountCreateEditDto(ACCOUNT_NUMBER, BigDecimal.valueOf(100.00));

    Set<ConstraintViolation<AccountCreateEditDto>> violations = validator.validate(dto);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());

    ConstraintViolation<AccountCreateEditDto> violation = violations.iterator().next();
    assertEquals("must match \"^\\d{4} \\d{4} \\d{4} \\d{4}$\"", violation.getMessage());
    assertEquals("accountNumber", violation.getPropertyPath().toString());
  }

  @Test
  void shouldFailValidationWhenBalanceIsNull() {
    AccountCreateEditDto dto = new AccountCreateEditDto(ACCOUNT_NUMBER, null);

    Set<ConstraintViolation<AccountCreateEditDto>> violations = validator.validate(dto);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());

    ConstraintViolation<AccountCreateEditDto> violation = violations.iterator().next();
    assertEquals("must not be null", violation.getMessage());
    assertEquals("balance", violation.getPropertyPath().toString());
  }

  @Test
  void shouldFailValidationWhenBalanceIsNegative() {
    AccountCreateEditDto dto = new AccountCreateEditDto(ACCOUNT_NUMBER, BigDecimal.valueOf(-100.00));

    Set<ConstraintViolation<AccountCreateEditDto>> violations = validator.validate(dto);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());

    ConstraintViolation<AccountCreateEditDto> violation = violations.iterator().next();
    assertEquals("must be greater than or equal to 0", violation.getMessage());
    assertEquals("balance", violation.getPropertyPath().toString());
  }
}
