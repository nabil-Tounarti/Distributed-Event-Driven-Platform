package com.ecommerce.user_service.exception.jwt;

public class JwtInvalidSignatureException extends JwtException {

  public JwtInvalidSignatureException() {
    super("JWT signature is invalid");
  }
}
