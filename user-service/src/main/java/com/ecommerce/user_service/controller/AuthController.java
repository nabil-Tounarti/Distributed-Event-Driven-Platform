package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dto.*;
import com.ecommerce.user_service.security.JwtService;
import com.ecommerce.user_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    LoginResponse loginResponse = authService.login(request);

    // Set refresh token as HttpOnly cookie — never exposed to JS
    ResponseCookie cookie =
        ResponseCookie.from("refreshToken", loginResponse.refreshToken())
            .httpOnly(true)
            .secure(true)
            .path("/auth/refresh")
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Strict")
            .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(loginResponse);
  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refresh(
      @CookieValue(name = "refreshToken") String refreshToken) {
    return ResponseEntity.ok(authService.refresh(refreshToken));
  }

  // The API Gateway calls this to get the public key for JWT verification
  @GetMapping("/jwks")
  public ResponseEntity<String> jwks() {
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(jwtService.getPublicKeyAsJwks());
  }
}
