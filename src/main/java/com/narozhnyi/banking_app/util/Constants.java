package com.narozhnyi.banking_app.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  @UtilityClass
  public class Regex {
    public static final String ACCOUNT_NUMBER_REGEX = "^\\d{4} \\d{4} \\d{4} \\d{4}$";
  }
}
