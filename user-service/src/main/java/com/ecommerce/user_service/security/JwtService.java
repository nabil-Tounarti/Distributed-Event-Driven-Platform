package com.ecommerce.user_service.security;

import com.ecommerce.user_service.exception.jwt.*;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jwt.*;
import jakarta.annotation.PostConstruct;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtService {

  @Value("${jwt.keystore.path}")
  private Resource keystorePath;

  @Value("${jwt.keystore.password}")
  private String keystorePassword;

  @Value("${jwt.keystore.alias}")
  private String keystoreAlias;

  @Value("${jwt.expiration-seconds:900}")
  private long expirationSeconds;

  private RSAKey rsaKey;

  @PostConstruct
  public void init() {
    try {
      KeyStore keyStore = KeyStore.getInstance("PKCS12");
      keyStore.load(keystorePath.getInputStream(), keystorePassword.toCharArray());

      RSAPublicKey publicKey = (RSAPublicKey) keyStore.getCertificate(keystoreAlias).getPublicKey();

      RSAPrivateKey privateKey =
          (RSAPrivateKey) keyStore.getKey(keystoreAlias, keystorePassword.toCharArray());

      this.rsaKey =
          new RSAKey.Builder(publicKey)
              .privateKey(privateKey)
              .keyID(UUID.randomUUID().toString())
              .algorithm(JWSAlgorithm.RS256)
              .keyUse(KeyUse.SIGNATURE)
              .build();

      log.info("JWT RSA key loaded successfully");

    } catch (Exception e) {
      log.error("Failed to load keystore", e);
      throw new JwtException("Failed to initialize JWT service", e);
    }
  }

  public String generateToken(UUID userId, String email, String role) {
    try {
      JWSSigner signer = new RSASSASigner(rsaKey);

      JWTClaimsSet claims =
          new JWTClaimsSet.Builder()
              .subject(userId.toString())
              .claim("email", email)
              .claim("role", role)
              .jwtID(UUID.randomUUID().toString())
              .issueTime(Date.from(Instant.now()))
              .expirationTime(Date.from(Instant.now().plusSeconds(expirationSeconds)))
              .build();

      SignedJWT jwt =
          new SignedJWT(
              new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.getKeyID()).build(), claims);

      jwt.sign(signer);
      return jwt.serialize();

    } catch (JOSEException e) {
      log.error("JWT generation failed", e);
      throw new JwtGenerationException("Failed to generate JWT", e);
    }
  }

  public String getPublicKeyAsJwks() {
    try {
      RSAKey publicKeyOnly = rsaKey.toPublicJWK();
      JWKSet jwkSet = new JWKSet(publicKeyOnly);
      return jwkSet.toString();

    } catch (Exception e) {
      log.error("JWKS generation failed", e);
      throw new JwtJwksException("Failed to build JWKS", e);
    }
  }

  public JWTClaimsSet validateToken(String token) {
    try {
      SignedJWT jwt = SignedJWT.parse(token);
      JWSVerifier verifier = new RSASSAVerifier(rsaKey.toPublicJWK());

      if (!jwt.verify(verifier)) {
        throw new JwtInvalidSignatureException();
      }

      JWTClaimsSet claims = jwt.getJWTClaimsSet();

      if (claims.getExpirationTime() == null || claims.getExpirationTime().before(new Date())) {
        throw new JwtExpiredException();
      }

      return claims;

    } catch (ParseException e) {
      log.error("JWT parsing failed", e);
      throw new JwtParsingException("Invalid JWT format", e);

    } catch (JOSEException e) {
      log.error("JWT verification failed", e);
      throw new JwtInvalidSignatureException();
    }
  }
}
