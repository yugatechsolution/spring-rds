package com.yuga.spring_rds.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.SpringRdsApplication;
import com.yuga.spring_rds.model.User;
import com.yuga.spring_rds.repository.UserRepository;
import com.yuga.spring_rds.util.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = SpringRdsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test") // Uses application-test.properties
@Rollback // Rolls back all DB changes after each test
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Enables @Order
class AuthControllerIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private ObjectMapper objectMapper;

  private static String jwtToken;

  @Test
  @Order(1)
  void testRegisterUser() throws Exception {
    User user = TestUtil.getUser();

    userRepository
        .findByUsernameOrEmail(user.getUsername(), user.getEmail())
        .ifPresent(userRepository::delete);

    log.info("📝 Registering a new user: {}", user.getUsername());
    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isOk());

    assertThat(userRepository.findByUsernameOrEmail(TestUtil.USERNAME, null)).isPresent();
  }

  @Test
  @Order(2)
  void testLoginUser() throws Exception {
    User user = TestUtil.getUser();

    log.info("🔑 Testing login with username: {}", user.getUsername());

    MvcResult result =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("id", TestUtil.USERNAME)
                    .queryParam("password", TestUtil.PASSWORD))
            .andDo(result1 -> log.info("Result: {}", result1.getResponse().getContentAsString()))
            .andExpect(status().isOk())
            .andReturn();

    jwtToken = result.getResponse().getContentAsString();
    log.info("✅ Received JWT Token: {}", jwtToken);

    assertThat(jwtToken).isNotNull();
  }

  @Test
  @Order(3)
  void testAccessProtectedEndpoint() throws Exception {
    if (jwtToken == null) {
      log.error("❌ JWT Token is null. Ensure login test runs first.");
      Assertions.fail("JWT Token should not be null.");
    }

    log.info("🔒 Testing access to protected endpoint with JWT token");

    mockMvc
        .perform(get("/api/contacts").header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk());
  }

  @Test
  @Order(4)
  void testAccessProtectedEndpointWithoutJWT() throws Exception {
    log.info("🚫 Testing access to protected endpoint without JWT");

    mockMvc.perform(get("/api/contacts")).andExpect(status().isUnauthorized());
  }
}
