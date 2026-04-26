package com.ecommerce.user_service.exception.jwt;

public class JwtExpiredException extends JwtException {

  public JwtExpiredException() {
    super("JWT token has expired");
  }
}
