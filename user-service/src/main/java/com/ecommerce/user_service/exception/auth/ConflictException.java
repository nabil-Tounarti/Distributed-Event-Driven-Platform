package com.ecommerce.user_service.exception.auth;

public class ConflictException extends RuntimeException {
  public ConflictException(String message) {
    super(message);
  }
}
