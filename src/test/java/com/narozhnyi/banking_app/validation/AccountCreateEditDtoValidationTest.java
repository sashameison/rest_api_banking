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
  void validAccountCreateEditDto() {
    AccountCreateEditDto dto = new AccountCreateEditDto("1234 5678 1234 5678", BigDecimal.valueOf(100.00));

    Set<ConstraintViolation<AccountCreateEditDto>> violations = validator.validate(dto);

    assertTrue(violations.isEmpty());
  }

  @Test
  void accountNumberIsNull_ShouldFailValidation() {
    AccountCreateEditDto dto = new AccountCreateEditDto(null, BigDecimal.valueOf(100.00));

    Set<ConstraintViolation<AccountCreateEditDto>> violations = validator.validate(dto);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());

    ConstraintViolation<AccountCreateEditDto> violation = violations.iterator().next();
    assertEquals("must not be null", violation.getMessage());
    assertEquals("accountNumber", violation.getPropertyPath().toString());
  }

  @Test
  void accountNumberDoesNotMatchPattern_ShouldFailValidation() {
    AccountCreateEditDto dto = new AccountCreateEditDto("1234567812345678", BigDecimal.valueOf(100.00));

    Set<ConstraintViolation<AccountCreateEditDto>> violations = validator.validate(dto);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());

    ConstraintViolation<AccountCreateEditDto> violation = violations.iterator().next();
    assertEquals("must match \"^\\d{4} \\d{4} \\d{4} \\d{4}$\"", violation.getMessage());
    assertEquals("accountNumber", violation.getPropertyPath().toString());
  }

  @Test
  void balanceIsNull_ShouldFailValidation() {
    AccountCreateEditDto dto = new AccountCreateEditDto("1234 5678 1234 5678", null);

    Set<ConstraintViolation<AccountCreateEditDto>> violations = validator.validate(dto);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());

    ConstraintViolation<AccountCreateEditDto> violation = violations.iterator().next();
    assertEquals("must not be null", violation.getMessage());
    assertEquals("balance", violation.getPropertyPath().toString());
  }

  @Test
  void balanceIsNegative_ShouldFailValidation() {
    AccountCreateEditDto dto = new AccountCreateEditDto("1234 5678 1234 5678", BigDecimal.valueOf(-100.00));

    Set<ConstraintViolation<AccountCreateEditDto>> violations = validator.validate(dto);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());

    ConstraintViolation<AccountCreateEditDto> violation = violations.iterator().next();
    assertEquals("must be greater than or equal to 0", violation.getMessage());
    assertEquals("balance", violation.getPropertyPath().toString());
  }
}
