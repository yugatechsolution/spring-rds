package com.yuga.spring_rds.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.model.User;
import com.yuga.spring_rds.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Uses application-test.properties
@Transactional // Ensures each test runs in a separate transaction
@Rollback // Rolls back all DB changes after each test
@Slf4j
class UserControllerIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void testRegisterUser() throws Exception {
    User user = new User();
    user.setUsername("testUser");
    user.setEmail("test@example.com");
    user.setPassword("password123");

    log.info("📝 Registering user: {}", user.getUsername());

    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("User registered successfully!")));

    log.info("✅ User registered successfully!");
  }

  @Test
  void testLoginWithUsername() throws Exception {
    User user = new User();
    user.setUsername("testUser");
    user.setEmail("test@example.com");
    user.setPassword("password123");
    userRepository.save(user);

    log.info("🔑 Testing login with username: {}", user.getUsername());

    mockMvc
        .perform(post("/auth/login").param("id", "testUser").param("password", "password123"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Login successful!")));

    log.info("✅ Login successful with username!");
  }

  @Test
  void testLoginWithEmail() throws Exception {
    User user = new User();
    user.setUsername("testUser");
    user.setEmail("test@example.com");
    user.setPassword("password123");
    userRepository.save(user);

    log.info("📧 Testing login with email: {}", user.getEmail());

    mockMvc
        .perform(
            post("/auth/login").param("id", "test@example.com").param("password", "password123"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Login successful!")));

    log.info("✅ Login successful with email!");
  }
}
