package com.narozhnyi.banking_app.exception;

public class AccountNotFound extends RuntimeException {

  public AccountNotFound(String message) {
    super(message);
  }
}
