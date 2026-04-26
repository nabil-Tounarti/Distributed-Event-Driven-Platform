package com.ecommerce.user_service.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ecommerce.user_service.config.AbstractIntegrationTest;
import com.ecommerce.user_service.dto.LoginRequest;
import com.ecommerce.user_service.dto.RegisterRequest;
import com.ecommerce.user_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
class AuthControllerIntegrationTest extends AbstractIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("Full Auth Lifecycle: Register -> Login -> Refresh")
  void fullAuthLifecycle() throws Exception {
    String email = "nabil@test.com";
    String password = "StrongPassword123!";

    // 1. Register
    RegisterRequest regRequest = new RegisterRequest(email, password, "nabil", "tounarti");
    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.accessToken").exists());

    assertThat(userRepository.findByEmail(email)).isPresent();

    // 2. Login
    LoginRequest loginRequest = new LoginRequest(email, password);
    MvcResult loginResult =
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(header().exists("Set-Cookie"))
            .andExpect(cookie().exists("refreshToken"))
            .andExpect(cookie().httpOnly("refreshToken", true))
            .andExpect(cookie().secure("refreshToken", true))
            .andReturn();

    Cookie refreshTokenCookie = loginResult.getResponse().getCookie("refreshToken");
    assertThat(refreshTokenCookie).isNotNull();

    // 3. Refresh (using cookie)
    mockMvc
        .perform(post("/auth/refresh").cookie(refreshTokenCookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.tokenType").value("Bearer"));
  }

  @Test
  @DisplayName("Should return 401 when refreshing with invalid cookie")
  void refresh_InvalidCookie() throws Exception {
    mockMvc
        .perform(post("/auth/refresh").cookie(new Cookie("refreshToken", "garbage-token")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return 409 when registering with existing email")
  void register_Conflict() throws Exception {
    String email = "conflict@example.com";
    String validPassword = "Password123!";
    RegisterRequest first = new RegisterRequest(email, validPassword, "A", "B");
    mockMvc.perform(
        post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(first)));

    RegisterRequest second = new RegisterRequest(email, validPassword, "C", "D");
    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(second)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("Email already registered"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  @DisplayName("Should return valid JWKS containing RSA public key")
  void getJwks_Contract() throws Exception {
    mockMvc
        .perform(get("/auth/jwks"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.keys").isArray())
        .andExpect(jsonPath("$.keys[0].kty").value("RSA"))
        .andExpect(jsonPath("$.keys[0].alg").value("RS256"))
        .andExpect(jsonPath("$.keys[0].use").value("sig"));
  }
}
