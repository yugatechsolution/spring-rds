package com.yuga.spring_rds.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.connector.WhatsAppConnector;
import com.yuga.spring_rds.model.api.request.WhatsAppWebhookRequest;
import com.yuga.spring_rds.service.WhatsAppWebhookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/whatsapp/callback")
public class WhatsAppWebhookController {

  private final ObjectMapper mapper = WhatsAppConnector.mapper;
  @Autowired private WhatsAppWebhookService whatsAppWebhookService;

  @GetMapping
  public ResponseEntity<String> verifyWebhook(
      @RequestParam("hub.mode") String mode,
      @RequestParam("hub.challenge") String challenge,
      @RequestParam("hub.verify_token") String verifyToken) {

    if ("subscribe".equals(mode) && "HI_THIS_IS_FROM_WHATSAPP".equals(verifyToken)) {
      log.info("Webhook verification successful.");
      return ResponseEntity.ok(challenge); // Meta requires the challenge to be returned
    }
    return ResponseEntity.status(403).body("Verification failed");
  }

  @PostMapping
  public ResponseEntity<String> handleIncomingMessage(@RequestBody JsonNode requestBody) {
    log.info("Received WhatsApp Message: {}", requestBody);
    try {
      WhatsAppWebhookRequest request =
          mapper.convertValue(requestBody, WhatsAppWebhookRequest.class);
      whatsAppWebhookService.processIncomingMessage(request);
      return ResponseEntity.ok("EVENT_RECEIVED");
    } catch (Exception e) {
      log.error("Error processing WhatsApp message: ", e);
      return ResponseEntity.status(500).body("Error processing message");
    }
  }
}
