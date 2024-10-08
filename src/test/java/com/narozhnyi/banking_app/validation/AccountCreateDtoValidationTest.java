package com.narozhnyi.banking_app.validation;

import com.narozhnyi.banking_app.dto.account.AccountCreateDto;
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

class AccountCreateDtoValidationTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldBeValidAccountCreateEditDto() {
    AccountCreateDto dto = new AccountCreateDto(ACCOUNT_NUMBER, BigDecimal.valueOf(100.00));

    Set<ConstraintViolation<AccountCreateDto>> violations = validator.validate(dto);

    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldFailValidationWhenAccountNumberIsNull() {
    AccountCreateDto dto = new AccountCreateDto(null, BigDecimal.valueOf(100.00));

    Set<ConstraintViolation<AccountCreateDto>> violations = validator.validate(dto);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());

    ConstraintViolation<AccountCreateDto> violation = violations.iterator().next();
    assertEquals("must not be null", violation.getMessage());
    assertEquals("accountNumber", violation.getPropertyPath().toString());
  }

  @Test
  void shouldFailValidationWhenAccountNumberDoesNotMatchPattern() {
    AccountCreateDto dto = new AccountCreateDto(ACCOUNT_NUMBER, BigDecimal.valueOf(100.00));

    Set<ConstraintViolation<AccountCreateDto>> violations = validator.validate(dto);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());

    ConstraintViolation<AccountCreateDto> violation = violations.iterator().next();
    assertEquals("must match \"^\\d{4} \\d{4} \\d{4} \\d{4}$\"", violation.getMessage());
    assertEquals("accountNumber", violation.getPropertyPath().toString());
  }

  @Test
  void shouldFailValidationWhenBalanceIsNull() {
    AccountCreateDto dto = new AccountCreateDto(ACCOUNT_NUMBER, null);

    Set<ConstraintViolation<AccountCreateDto>> violations = validator.validate(dto);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());

    ConstraintViolation<AccountCreateDto> violation = violations.iterator().next();
    assertEquals("must not be null", violation.getMessage());
    assertEquals("balance", violation.getPropertyPath().toString());
  }

  @Test
  void shouldFailValidationWhenBalanceIsNegative() {
    AccountCreateDto dto = new AccountCreateDto(ACCOUNT_NUMBER, BigDecimal.valueOf(-100.00));

    Set<ConstraintViolation<AccountCreateDto>> violations = validator.validate(dto);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());

    ConstraintViolation<AccountCreateDto> violation = violations.iterator().next();
    assertEquals("must be greater than or equal to 0", violation.getMessage());
    assertEquals("balance", violation.getPropertyPath().toString());
  }
}
