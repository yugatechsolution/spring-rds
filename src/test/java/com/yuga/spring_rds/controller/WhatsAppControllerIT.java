package com.yuga.spring_rds.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.SpringRdsApplication;
import com.yuga.spring_rds.model.request.BroadcastMessageRequest;
import com.yuga.spring_rds.model.whatsapp.WhatsAppMessageType;
import com.yuga.spring_rds.model.whatsapp.response.WhatsAppMessageResponseModel;
import com.yuga.spring_rds.service.WhatsAppService;
import com.yuga.spring_rds.util.JwtUtil;
import com.yuga.spring_rds.util.TestUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = SpringRdsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test") // Uses application-test.properties
@Rollback // Rolls back all DB changes after each test
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Enables @Order
class WhatsAppControllerIT {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private WhatsAppService whatsAppService;

  private String jwtToken;

  @BeforeEach
  void setUp() {
    // Generate a test user and JWT token
    jwtToken = "Bearer " + JwtUtil.generateToken(TestUtil.getUser().getId());
  }

  @Test
  @Order(1)
  void testBroadcastMessage_Success() throws Exception {
    // Create a valid broadcast message request
    BroadcastMessageRequest request =
        BroadcastMessageRequest.builder()
            .whatsAppMessageType(WhatsAppMessageType.TEXT)
            .text("Hi! Sending this message from APP!")
            .phoneNumbers(List.of("+919008777044"))
            .build();

    String requestJson = objectMapper.writeValueAsString(request);

    // Call the API
    String responseJson =
        mockMvc
            .perform(
                post("/api/whatsapp/broadcast")
                    .header("Authorization", jwtToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.messages").isArray())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Deserialize response and assert
    WhatsAppMessageResponseModel response =
        objectMapper.readValue(responseJson, WhatsAppMessageResponseModel.class);
    assertThat(response.getMessages()).isNotEmpty();
  }
}
