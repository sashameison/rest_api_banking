package com.narozhnyi.banking_app.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

  private String message;
  private int statusCode;
  private String uri;
}
