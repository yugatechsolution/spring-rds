package com.yuga.spring_rds.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.SpringRdsApplication;
import com.yuga.spring_rds.connector.WhatsAppConnector;
import com.yuga.spring_rds.model.api.request.WhatsAppWebhookRequest;
import com.yuga.spring_rds.service.WhatsAppWebhookService;
import com.yuga.spring_rds.util.TestUtil;
import java.util.UUID;
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
class WhatsAppWebhookControllerIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private WhatsAppWebhookService whatsAppWebhookService;

  private ObjectMapper objectMapper = WhatsAppConnector.mapper;

  private WhatsAppWebhookRequest request;

  @Test
  void handleIncomingMessage_shouldReturn200_whenRequestIsValid() throws Exception {
    request = TestUtil.getWhatsAppWebhookRequest();
    request
        .getEntry()
        .get(0)
        .getChanges()
        .get(0)
        .getValue()
        .getMessages()
        .get(0)
        .setId(UUID.randomUUID().toString());
    mockMvc
        .perform(
            post("/api/whatsapp/callback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    //
    // verify(whatsAppWebhookService).processIncomingMessage(any(WhatsAppWebhookRequest.class));
  }

  //    @Test
  //    void handleIncomingMessage_shouldReturn500_whenProcessingFails() throws Exception {
  //      doThrow(new RuntimeException("Test Exception"))
  //
  // .when(whatsAppWebhookService).processIncomingMessage(any(WhatsAppWebhookRequest.class));
  //
  //      mockMvc.perform(post("/api/whatsapp/callback")
  //              .contentType(MediaType.APPLICATION_JSON)
  //              .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isInternalServerError());
  //    }
}
