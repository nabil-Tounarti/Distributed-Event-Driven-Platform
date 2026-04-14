package com.ecommerce.user_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.ecommerce.user_service.dto.*;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.entity.UserRole;
import com.ecommerce.user_service.exception.auth.AuthenticationException;
import com.ecommerce.user_service.exception.auth.ConflictException;
import com.ecommerce.user_service.repository.UserRepository;
import com.ecommerce.user_service.security.JwtService;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;
  @Mock private StringRedisTemplate redis;
  @Mock private ValueOperations<String, String> valueOperations;

  @InjectMocks private AuthService authService;

  private final UUID userId = UUID.randomUUID();
  private final String email = "test@example.com";
  private final String password = "password123";

  @BeforeEach
  void setUp() {
    lenient().when(redis.opsForValue()).thenReturn(valueOperations);
  }

  @Nested
  @DisplayName("Registration Tests")
  class Registration {

    @Test
    @DisplayName("Should register a new user successfully")
    void register_Success() {
      RegisterRequest request = new RegisterRequest(email, password, "First", "Last");
      when(userRepository.existsByEmail(email)).thenReturn(false);
      when(passwordEncoder.encode(password)).thenReturn("hashedPassword");
      when(jwtService.generateToken(any(), eq(email), anyString())).thenReturn("accessToken");

      AuthResponse response = authService.register(request);

      assertThat(response.accessToken()).isEqualTo("accessToken");
      verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when email exists")
    void register_DuplicateEmail() {
      RegisterRequest request = new RegisterRequest(email, password, "First", "Last");
      when(userRepository.existsByEmail(email)).thenReturn(true);

      assertThatThrownBy(() -> authService.register(request))
          .isInstanceOf(ConflictException.class)
          .hasMessage("Email already registered");

      verify(userRepository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("Login Tests")
  class Login {

    @Test
    @DisplayName("Should login successfully and return tokens")
    void login_Success() {
      LoginRequest request = new LoginRequest(email, password);
      User user = new User();
      user.setId(userId);
      user.setEmail(email);
      user.setPasswordHash("hashedPassword");
      user.setRole(UserRole.USER);

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(passwordEncoder.matches(password, "hashedPassword")).thenReturn(true);
      when(jwtService.generateToken(userId, email, "USER")).thenReturn("accessToken");

      LoginResponse response = authService.login(request);

      assertThat(response.accessToken()).isEqualTo("accessToken");
      assertThat(response.refreshToken()).isNotNull();
      verify(valueOperations).set(anyString(), eq(userId.toString()), any(Duration.class));
    }

    @Test
    @DisplayName("Should fail with invalid credentials (wrong password)")
    void login_InvalidPassword() {
      LoginRequest request = new LoginRequest(email, password);
      User user = new User();
      user.setPasswordHash("hashedPassword");

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(passwordEncoder.matches(eq(password), anyString())).thenReturn(false);

      assertThatThrownBy(() -> authService.login(request))
          .isInstanceOf(AuthenticationException.class)
          .hasMessage("Invalid credentials");
    }

    @Test
    @DisplayName("Should use dummy hash for nonexistent user to prevent timing attacks")
    void login_NonExistentUser_PreventsTimingAttack() {
      LoginRequest request = new LoginRequest(email, password);
      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> authService.login(request))
          .isInstanceOf(AuthenticationException.class);

      verify(passwordEncoder).matches(eq(password), contains("dummy"));
    }
  }

  @Nested
  @DisplayName("Token Refresh Tests")
  class Refresh {

    @Test
    @DisplayName("Should rotate refresh token successfully")
    void refresh_Success() {
      String oldToken = "old-refresh-token";
      when(valueOperations.get("refresh:" + oldToken)).thenReturn(userId.toString());

      User user = new User();
      user.setId(userId);
      user.setEmail(email);
      user.setRole(UserRole.USER);
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      when(jwtService.generateToken(userId, email, "USER")).thenReturn("newAccessToken");

      LoginResponse response = authService.refresh(oldToken);

      assertThat(response.accessToken()).isEqualTo("newAccessToken");
      assertThat(response.refreshToken()).isNotEqualTo(oldToken);
      verify(redis).delete("refresh:" + oldToken);
      verify(valueOperations)
          .set(startsWith("refresh:"), eq(userId.toString()), any(Duration.class));
    }

    @Test
    @DisplayName("Should throw AuthenticationException for invalid refresh token")
    void refresh_InvalidToken() {
      when(valueOperations.get(anyString())).thenReturn(null);

      assertThatThrownBy(() -> authService.refresh("fakeToken"))
          .isInstanceOf(AuthenticationException.class)
          .hasMessage("Refresh token invalid or expired");
    }
  }
}
