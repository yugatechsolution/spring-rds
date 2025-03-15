package com.yuga.spring_rds.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.SpringRdsApplication;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.interactive.InteractiveListMessageRequest;
import com.yuga.spring_rds.dto.ChatbotMessageDTO;
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

  @BeforeEach
  void setUp() {
    jwtToken = "Bearer " + JwtUtil.generateToken(TestUtil.getUser().getUsername());
  }

  @Test
  @Order(1)
  void testInteractiveListMessage_Success() throws Exception {
    // Create a valid chatbot message request
    ChatbotMessageDTO request = TestUtil.getChatbotMessage();
    callApiAndAddAssertions(request);
  }

  private void callApiAndAddAssertions(ChatbotMessageDTO request) throws Exception {
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

    ChatbotMessageDTO response = objectMapper.readValue(responseJson, ChatbotMessageDTO.class);

    assertThat(response).isNotNull();
    assertThat(response.getTriggerText()).isEqualTo(request.getTriggerText());
    assertThat(response.getWhatsAppMessageRequests()).isNotNull();
    assertThat(
            ((InteractiveListMessageRequest)
                    (response.getWhatsAppMessageRequests().get(0).getInteractive()))
                .getAction()
                .getSections())
        .hasSize(2);
  }
}
