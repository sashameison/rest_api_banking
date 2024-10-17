package com.narozhnyi.banking_app.exception;

public class NotEnoughMoneyException extends RuntimeException {

  public NotEnoughMoneyException(String message) {
    super(message);
  }
}
