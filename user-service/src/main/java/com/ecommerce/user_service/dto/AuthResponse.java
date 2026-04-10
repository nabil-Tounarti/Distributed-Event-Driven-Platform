package com.ecommerce.user_service.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresIn) {
  public static AuthResponse of(String token) {
    return new AuthResponse(token, "Bearer", 900); // 15 min
  }
}
