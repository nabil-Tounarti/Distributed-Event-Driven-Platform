package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.*;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.entity.UserRole;
import com.ecommerce.user_service.exception.auth.AuthenticationException;
import com.ecommerce.user_service.exception.auth.ConflictException;
import com.ecommerce.user_service.exception.auth.ResourceNotFoundException;
import com.ecommerce.user_service.repository.UserRepository;
import com.ecommerce.user_service.security.JwtService;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final StringRedisTemplate redis;

  private static final String REFRESH_TOKEN_PREFIX = "refresh:";
  private static final Long REFRESH_TOKEN_TTL = 900L;
  // Dummy hash for timing attack prevention — same cost factor as real hashes
  private static final String DUMMY_HASH =
      "$2a$12$dummyhashtopreventtimingattackxxxxxxxxxxxxxxxxxxxxxx";

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new ConflictException("Email already registered");
    }

    User user = new User();
    user.setEmail(request.email());
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setFirstName(request.firstName());
    user.setLastName(request.lastName());
    user.setRole(UserRole.USER);

    userRepository.save(user);
    log.info("User registered: email={}", user.getEmail());

    return buildAuthResponse(user);
  }

  public LoginResponse login(LoginRequest request) {
    Optional<User> userOpt = userRepository.findByEmail(request.email());

    // This prevents timing attacks — response time is always ~100ms
    String hashToCheck = userOpt.map(User::getPasswordHash).orElse(DUMMY_HASH);

    boolean passwordMatches = passwordEncoder.matches(request.password(), hashToCheck);

    if (userOpt.isEmpty() || !passwordMatches) {
      // Same message always — never reveal whether email exists
      throw new AuthenticationException("Invalid credentials");
    }

    User user = userOpt.get();
    String accessToken = buildAccessToken(user);
    String refreshToken = generateRefreshToken(user.getId());

    log.info("User logged in: userId={}", user.getId()); // log ID not email in prod
    return new LoginResponse(accessToken, refreshToken, "Bearer", REFRESH_TOKEN_TTL);
  }

  public LoginResponse refresh(String refreshToken) {
    String key = REFRESH_TOKEN_PREFIX + refreshToken;
    String userId = redis.opsForValue().get(key);

    if (userId == null) {
      throw new AuthenticationException("Refresh token invalid or expired");
    }

    // Rotate: delete old token, issue new pair
    redis.delete(key);

    User user =
        userRepository
            .findById(UUID.fromString(userId))
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    String newAccessToken = buildAccessToken(user);
    String newRefreshToken = generateRefreshToken(user.getId());

    log.debug("Refresh token rotated for userId={}", userId);

    return new LoginResponse(newAccessToken, newRefreshToken, "Bearer", REFRESH_TOKEN_TTL);
  }

  private String buildAccessToken(User user) {
    return jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().toString());
  }

  private String generateRefreshToken(UUID userId) {
    String token = UUID.randomUUID().toString();
    Duration ttl = Duration.ofDays(REFRESH_TOKEN_TTL);
    redis.opsForValue().set(REFRESH_TOKEN_PREFIX + token, userId.toString(), ttl);
    return token;
  }

  private AuthResponse buildAuthResponse(User user) {
    return AuthResponse.of(buildAccessToken(user));
  }
}
