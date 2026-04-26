package com.ecommerce.user_service.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ecommerce.user_service.config.AbstractIntegrationTest;
import com.ecommerce.user_service.exception.jwt.JwtInvalidSignatureException;
import com.nimbusds.jwt.JWTClaimsSet;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class JwtServiceTest extends AbstractIntegrationTest {

  @Autowired private JwtService jwtService;

  private final UUID userId = UUID.randomUUID();
  private final String email = "test@faang.com";
  private final String role = "USER";

  @Test
  @DisplayName("Should generate and validate a token successfully")
  void tokenLifecycle_Success() {
    String token = jwtService.generateToken(userId, email, role);
    assertThat(token).isNotNull();

    JWTClaimsSet claims = jwtService.validateToken(token);
    assertThat(claims.getSubject()).isEqualTo(userId.toString());
    assertThat(claims.getClaim("email")).isEqualTo(email);
    assertThat(claims.getClaim("role")).isEqualTo(role);
  }

  @Test
  @DisplayName("Should fail validation for an invalid signature")
  void validateToken_InvalidSignature() {
    String token = jwtService.generateToken(userId, email, role);
    // Tamper with the token (keep header/body structure but break signature part)
    String tamperedToken = token.substring(0, token.lastIndexOf(".") + 1) + "invalidSignature";

    assertThatThrownBy(() -> jwtService.validateToken(tamperedToken))
        .isInstanceOf(JwtInvalidSignatureException.class);
  }

  @Test
  @DisplayName("Should export public key in valid JWKS format")
  void getPublicKeyAsJwks_Format() {
    String jwks = jwtService.getPublicKeyAsJwks();
    assertThat(jwks).contains("\"keys\":");
    assertThat(jwks).contains("\"kty\":\"RSA\"");
    assertThat(jwks).contains("\"alg\":\"RS256\"");
  }
}
