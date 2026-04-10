package com.ecommerce.user_service.exception.jwt;

public class JwtParsingException extends JwtException {

  public JwtParsingException(String message, Throwable cause) {
    super(message, cause);
  }
}
