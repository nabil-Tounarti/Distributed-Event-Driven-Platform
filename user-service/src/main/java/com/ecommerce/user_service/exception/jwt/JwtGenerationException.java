package com.ecommerce.user_service.exception.jwt;

public class JwtGenerationException extends JwtException {

  public JwtGenerationException(String message, Throwable cause) {
    super(message, cause);
  }
}
