package com.ecommerce.user_service.dto;

public record LoginResponse(
    String accessToken, String refreshToken, String tokenType, long expiresIn) {}
