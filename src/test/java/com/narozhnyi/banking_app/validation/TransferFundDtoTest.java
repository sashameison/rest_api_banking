package com.narozhnyi.banking_app.validation;

import com.narozhnyi.banking_app.dto.transaction.TransferFundDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferFundDtoTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void whenValidTransferFundDto_thenNoViolations() {
    // Given a valid TransferFundDto
    TransferFundDto transferFundDto = new TransferFundDto(
        BigDecimal.valueOf(100),
        "1234 5678 1234 5678",
        "8765 4321 8765 4321"
    );

    // When validating the DTO
    Set<ConstraintViolation<TransferFundDto>> violations = validator.validate(transferFundDto);

    // Then no validation violations should occur
    assertTrue(violations.isEmpty());
  }

  @Test
  void whenNegativeTransferAmount_thenViolations() {
    // Given a TransferFundDto with a negative transfer amount
    TransferFundDto transferFundDto = new TransferFundDto(
        BigDecimal.valueOf(-100),
        "1234 5678 1234 5678",
        "8765 4321 8765 4321"
    );

    // When validating the DTO
    Set<ConstraintViolation<TransferFundDto>> violations = validator.validate(transferFundDto);

    // Then a violation should occur due to the negative transfer amount
    assertEquals(1, violations.size());
    ConstraintViolation<TransferFundDto> violation = violations.iterator().next();
    assertEquals("must be greater than 0", violation.getMessage());
  }

  @Test
  void whenInvalidSenderAccountNumber_thenViolations() {
    // Given a TransferFundDto with an invalid sender account number
    TransferFundDto transferFundDto = new TransferFundDto(
        BigDecimal.valueOf(100),
        "invalid_account",
        "8765 4321 8765 4321"
    );

    // When validating the DTO
    Set<ConstraintViolation<TransferFundDto>> violations = validator.validate(transferFundDto);

    // Then a violation should occur due to the invalid sender account number
    assertEquals(1, violations.size());
    ConstraintViolation<TransferFundDto> violation = violations.iterator().next();
    assertEquals("must match \"^\\d{4} \\d{4} \\d{4} \\d{4}$\"", violation.getMessage());
  }

  @Test
  void whenInvalidReceiverAccountNumber_thenViolations() {
    // Given a TransferFundDto with an invalid receiver account number
    TransferFundDto transferFundDto = new TransferFundDto(
        BigDecimal.valueOf(100),
        "1234 5678 1234 5678",
        "invalid_account"
    );

    // When validating the DTO
    Set<ConstraintViolation<TransferFundDto>> violations = validator.validate(transferFundDto);

    // Then a violation should occur due to the invalid receiver account number
    assertEquals(1, violations.size());
    ConstraintViolation<TransferFundDto> violation = violations.iterator().next();
    assertEquals("must match \"^\\d{4} \\d{4} \\d{4} \\d{4}$\"", violation.getMessage());
  }

  @Test
  void whenAllFieldsInvalid_thenMultipleViolations() {
    // Given a TransferFundDto with invalid transfer amount and account numbers
    TransferFundDto transferFundDto = new TransferFundDto(
        BigDecimal.valueOf(-100),
        "invalid_sender",
        "invalid_receiver"
    );

    // When validating the DTO
    Set<ConstraintViolation<TransferFundDto>> violations = validator.validate(transferFundDto);

    // Then multiple violations should occur
    assertEquals(3, violations.size());
  }
}