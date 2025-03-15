package com.yuga.spring_rds.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.SpringRdsApplication;
import com.yuga.spring_rds.dto.ChatbotFlowDTO;
import com.yuga.spring_rds.util.JwtUtil;
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

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = SpringRdsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Rollback
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChatbotControllerIT {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private String jwtToken;
  private static String triggerText = "Test Trigger";

  @BeforeEach
  void setUp() {
    jwtToken = "Bearer " + JwtUtil.generateToken(TestUtil.getUser().getUsername());
  }

  @Test
  @Order(1)
  void testCreateChatbotFlow_Success() throws Exception {
    log.info("Starting test for chatbot flow creation");

    // Create chatbot flow request
    ChatbotFlowDTO request = TestUtil.getChatbotMessage();
    triggerText = request.getTriggerText();
    String requestJson = objectMapper.writeValueAsString(request);

    String responseJson =
        mockMvc
            .perform(
                post("/api/chatbot")
                    .header("Authorization", jwtToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ChatbotFlowDTO response = objectMapper.readValue(responseJson, ChatbotFlowDTO.class);

    log.info("Chatbot flow created successfully: {}", response);

    assertThat(response).isNotNull();
    assertThat(response.getTriggerText()).isEqualTo(request.getTriggerText());
    assertThat(response.getWhatsAppMessageRequests()).isNotEmpty();
  }

  @Test
  @Order(2)
  void testGetChatbotFlow_Success() throws Exception {
    log.info("Starting test for fetching chatbot flow");

    String responseJson =
        mockMvc
            .perform(
                get("/api/chatbot")
                    .header("Authorization", jwtToken)
                    .param("triggerText", triggerText))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ChatbotFlowDTO response = objectMapper.readValue(responseJson, ChatbotFlowDTO.class);

    log.info("Fetched chatbot flow: {}", response);

    assertThat(response).isNotNull();
    assertThat(response.getTriggerText()).isEqualTo(triggerText);
    assertThat(response.getWhatsAppMessageRequests()).isNotEmpty();
  }

  @Test
  @Order(3)
  void testDeleteChatbotFlow_Success() throws Exception {
    log.info("Starting test for chatbot flow deletion");

    mockMvc
        .perform(
            delete("/api/chatbot")
                .header("Authorization", jwtToken)
                .param("triggerText", triggerText))
        .andExpect(status().isNoContent());

    log.info("Chatbot flow deleted successfully");

    // Verify that the flow is deleted by attempting to fetch it
    mockMvc
        .perform(
            get("/api/chatbot").header("Authorization", jwtToken).param("triggerText", triggerText))
        .andExpect(status().isNotFound());

    log.info("Confirmed that chatbot flow with triggerText={} is deleted", triggerText);
  }
}
