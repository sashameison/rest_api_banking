package com.narozhnyi.banking_app.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  @UtilityClass
  public class Regex {
    public static final String ACCOUNT_NUMBER_REGEX = "^\\d{4} \\d{4} \\d{4} \\d{4}$";
  }

  @UtilityClass
  public class Errors {
    public static final String ACCOUNT_NOT_FOUND_ERROR = "Account not found for account number: %s";
    public static final String ACCOUNT_ALREADY_EXIST_ERROR = "Account already exist with this account number";
    public static final String NOT_ENOUGH_MONEY = "Not enough money to transfer.";
  }
}
