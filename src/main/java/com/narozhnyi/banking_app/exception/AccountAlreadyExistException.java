package com.narozhnyi.banking_app.exception;

public class AccountAlreadyExistException extends RuntimeException {

  public AccountAlreadyExistException(String message) {
    super(message);
  }
}
