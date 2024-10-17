package com.narozhnyi.banking_app.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.narozhnyi.banking_app.exception.AccountNotFound;
import com.narozhnyi.banking_app.exception.NotEnoughMoneyException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(AccountNotFound.class)
  public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFound ex, HttpServletRequest request) {
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
