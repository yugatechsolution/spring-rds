package com.yuga.spring_rds.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.model.request.BroadcastMessageRequest;
import com.yuga.spring_rds.model.request.WhatsAppWebhookRequest;
import com.yuga.spring_rds.model.response.BroadcastMessageTemplateResponse;
import com.yuga.spring_rds.service.WhatsAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/whatsapp")
public class WhatsAppController {

  @Autowired private WhatsAppService whatsappService;

  @GetMapping("/callback")
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

  @PostMapping("/callback")
  public ResponseEntity<String> handleIncomingMessage(@RequestBody JsonNode requestBody) {
    log.info("Received WhatsApp Message: {}", requestBody);
    try {
      WhatsAppWebhookRequest request =
          new ObjectMapper().convertValue(requestBody, WhatsAppWebhookRequest.class);

      if (request.getEntry() != null && !request.getEntry().isEmpty()) {
        var entry = request.getEntry().getFirst();
        var changes = entry.getChanges().getFirst();
        var value = changes.getValue();

        if (value.getMessages() != null && !value.getMessages().isEmpty()) {
          var message = value.getMessages().getFirst();
          String from = message.getFrom();
          String text = message.getText().getBody();

          log.info("Received message from: " + from + ", Text: " + text);

          // Process the message (pass it to the service layer)
          //          whatsappService.processIncomingMessage(from, text);
        }
      }

      return ResponseEntity.ok("EVENT_RECEIVED");
    } catch (Exception e) {
      log.error("Error processing WhatsApp message: ", e);
      return ResponseEntity.status(500).body("Error processing message");
    }
  }

  @PostMapping("/broadcast")
  public ResponseEntity<BroadcastMessageTemplateResponse> broadcastMessage(
      @RequestBody BroadcastMessageRequest request) {
    BroadcastMessageTemplateResponse response =
        whatsappService.broadcastWhatsAppMessageTemplate(request);
    return ResponseEntity.ok(response);
  }
}
