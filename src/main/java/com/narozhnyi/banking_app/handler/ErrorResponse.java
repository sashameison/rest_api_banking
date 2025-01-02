package com.narozhnyi.banking_app.handler;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

  private String message;
  private Map<String, Object> errorDetails;
  private int statusCode;
  private String uri;

  public ErrorResponse(String message, int statusCode, String uri) {
    this.message = message;
    this.errorDetails = new HashMap<>();
    this.statusCode = statusCode;
    this.uri = uri;
  }
}
