package com.narozhnyi.banking_app.validation;

import static com.narozhnyi.banking_app.service.TransactionServiceTest.ACCOUNT_NUMBER;
import static com.narozhnyi.banking_app.service.TransactionServiceTest.RECEIVER_ACCOUNT_NUMBER;
import static com.narozhnyi.banking_app.util.Constants.Errors.ACCOUNT_NUMBER_INVALID_ERROR;
import static com.narozhnyi.banking_app.util.Constants.Errors.AMOUNT_INVALID_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;

import com.narozhnyi.banking_app.dto.transaction.TransferFundDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransferFundDtoTest {

  public static final String INVALID_ACCOUNT = "invalid_account";
  public static final String INVALID_SENDER = "invalid_sender";
  public static final String INVALID_RECEIVER = "invalid_receiver";
  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void whenValidTransferFundDto_thenNoViolations() {
    TransferFundDto transferFundDto = new TransferFundDto(
        BigDecimal.valueOf(100),
        ACCOUNT_NUMBER,
        RECEIVER_ACCOUNT_NUMBER
    );

    Set<ConstraintViolation<TransferFundDto>> violations = validator.validate(transferFundDto);

    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldNotTransferAmountWhenViolations() {
    TransferFundDto transferFundDto = new TransferFundDto(
        BigDecimal.valueOf(-100),
        ACCOUNT_NUMBER,
        RECEIVER_ACCOUNT_NUMBER
    );

    Set<ConstraintViolation<TransferFundDto>> violations = validator.validate(transferFundDto);

    assertEquals(1, violations.size());
    ConstraintViolation<TransferFundDto> violation = violations.iterator().next();
    assertEquals(AMOUNT_INVALID_ERROR, violation.getMessage());
  }

  @Test
  void whenInvalidSenderAccountNumberWhenViolations() {
    TransferFundDto transferFundDto = new TransferFundDto(
        BigDecimal.valueOf(100),
        INVALID_ACCOUNT,
        RECEIVER_ACCOUNT_NUMBER
    );

    Set<ConstraintViolation<TransferFundDto>> violations = validator.validate(transferFundDto);

    assertEquals(1, violations.size());
    ConstraintViolation<TransferFundDto> violation = violations.iterator().next();
    assertEquals(ACCOUNT_NUMBER_INVALID_ERROR, violation.getMessage());
  }

  @Test
  void whenInvalidReceiverAccountNumberWhenViolations() {
    TransferFundDto transferFundDto = new TransferFundDto(
        BigDecimal.valueOf(100),
        ACCOUNT_NUMBER,
        INVALID_ACCOUNT
    );

    Set<ConstraintViolation<TransferFundDto>> violations = validator.validate(transferFundDto);

    assertEquals(1, violations.size());
    ConstraintViolation<TransferFundDto> violation = violations.iterator().next();
    assertEquals(ACCOUNT_NUMBER_INVALID_ERROR, violation.getMessage());
  }

  @Test
  void whenAllFieldsInvalidWhenMultipleViolations() {
    // Given a TransferFundDto with invalid transfer amount and account numbers
    TransferFundDto transferFundDto = new TransferFundDto(
        BigDecimal.valueOf(-100),
        INVALID_SENDER,
        INVALID_RECEIVER
    );

    // When validating the DTO
    Set<ConstraintViolation<TransferFundDto>> violations = validator.validate(transferFundDto);

    // Then multiple violations should occur
    assertEquals(3, violations.size());
  }
}