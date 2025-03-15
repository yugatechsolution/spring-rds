package com.yuga.spring_rds.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.SpringRdsApplication;
import com.yuga.spring_rds.domain.whatsapp.WhatsAppMessageType;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.TextMessageRequest;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.interactive.InteractiveListMessageRequest;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.interactive.InteractiveMessageType;
import com.yuga.spring_rds.domain.whatsapp.util.Text;
import com.yuga.spring_rds.model.api.request.WhatsAppMessageRequest;
import com.yuga.spring_rds.model.api.response.SendMessageResponse;
import com.yuga.spring_rds.service.SendMessageService;
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
  @Autowired private SendMessageService sendMessageService;

  private String jwtToken;

  @BeforeEach
  void setUp() {
    // Generate a test user and JWT token
    jwtToken = "Bearer " + JwtUtil.generateToken(TestUtil.getUser().getUsername());
  }

  @Test
  @Order(1)
  void testTextMessage_Success() throws Exception {
    // Create a valid broadcast message request
    String phoneNumber = "+916301472014";
    WhatsAppMessageRequest request =
        WhatsAppMessageRequest.builder()
            .phoneNumbers(List.of(phoneNumber))
            .type(WhatsAppMessageType.text)
            .text(
                TextMessageRequest.builder()
                    .body("testBroadcastMessage_Success")
                    .previewUrl(true)
                    .build())
            .build();
    callApiAndAddAssertions(request);
  }

  @Test
  @Order(1)
  void testListMessage_Success() throws Exception {
    // Create a valid broadcast message request
    String phoneNumber = "+916301472014";
    WhatsAppMessageRequest request =
        WhatsAppMessageRequest.builder()
            .phoneNumbers(List.of(phoneNumber))
            .type(WhatsAppMessageType.interactive)
            .interactive(
                InteractiveListMessageRequest.builder()
                    .type(InteractiveMessageType.LIST)
                    .header(
                        InteractiveListMessageRequest.Header.builder()
                            .type("text")
                            .text("LIST HEADER!")
                            .build())
                    .body(Text.builder().text("list bodyyyy").build())
                    .footer(Text.builder().text("list FOOTERRRR").build())
                    .action(
                        InteractiveListMessageRequest.Action.builder()
                            .button("Button Text")
                            .sections(
                                List.of(
                                    InteractiveListMessageRequest.Section.builder()
                                        .title("List Section 1 Title")
                                        .rows(
                                            List.of(
                                                InteractiveListMessageRequest.Row.builder()
                                                    .id("LIST_SECTION_1_ROW_1_ID")
                                                    .title("SECTION_1_ROW_1_TITLE")
                                                    .description("SECTION_1_ROW_1_DESC")
                                                    .build(),
                                                InteractiveListMessageRequest.Row.builder()
                                                    .id("<LIST_SECTION_1_ROW_2_ID>")
                                                    .title("<SECTION_1_ROW_2_TITLE>")
                                                    .description("<SECTION_1_ROW_2_DESC>")
                                                    .build()))
                                        .build(),
                                    InteractiveListMessageRequest.Section.builder()
                                        .title("<LIST_SECTION_2_TITLE>")
                                        .rows(
                                            List.of(
                                                InteractiveListMessageRequest.Row.builder()
                                                    .id("<LIST_SECTION_2_ROW_1_ID>")
                                                    .title("<SECTION_2_ROW_1_TITLE>")
                                                    .description("<SECTION_2_ROW_1_DESC>")
                                                    .build(),
                                                InteractiveListMessageRequest.Row.builder()
                                                    .id("<LIST_SECTION_2_ROW_2_ID>")
                                                    .title("<SECTION_2_ROW_2_TITLE>")
                                                    .description("<SECTION_2_ROW_2_DESC>")
                                                    .build()))
                                        .build()))
                            .build())
                    .build())
            .build();
    callApiAndAddAssertions(request);
  }

  private SendMessageResponse callApiAndAddAssertions(WhatsAppMessageRequest request)
      throws Exception {
    String requestJson = objectMapper.writeValueAsString(request);
    // Call the API
    String responseJson =
        mockMvc
            .perform(
                post("/api/whatsapp/send")
                    .header("Authorization", jwtToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath(
                        "$.responseDetails['"
                            + request.getPhoneNumbers().getFirst()
                            + "'].messages")
                    .isArray())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Deserialize response and assert
    SendMessageResponse response = objectMapper.readValue(responseJson, SendMessageResponse.class);
    assertThat(response.getResponseDetails()).isNotEmpty();
    return response;
  }
}
