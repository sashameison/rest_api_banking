package com.narozhnyi.banking_app.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.narozhnyi.banking_app.exception.AccountNotFoundException;
import com.narozhnyi.banking_app.exception.NotEnoughMoneyException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex, HttpServletRequest request) {
    var errorResponse = new ErrorResponse(
        ex.getMessage(),
        NOT_FOUND.value(),
        request.getRequestURI());
    return new ResponseEntity<>(errorResponse, NOT_FOUND);
  }

  @ExceptionHandler(NotEnoughMoneyException.class)
  public ResponseEntity<ErrorResponse> handleNotEnoughMoneyException(
      NotEnoughMoneyException ex,
      HttpServletRequest request) {
    var errorResponse = new ErrorResponse(
        ex.getMessage(),
        BAD_REQUEST.value(),
        request.getRequestURI());
    return new ResponseEntity<>(errorResponse, BAD_REQUEST);
  }
}
