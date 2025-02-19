package com.yuga.spring_rds.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable) // ❌ Disable CSRF
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/**")
                    .permitAll() // ✅ Allow auth APIs
                    .anyRequest()
                    .authenticated()); // Require authentication for everything else

    return http.build();
  }
}
