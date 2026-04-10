package com.ecommerce.user_service.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@TestConfiguration
public class TestRedisConfig {

  @Bean
  public StringRedisTemplate stringRedisTemplate() {
    return Mockito.mock(StringRedisTemplate.class);
  }
}
