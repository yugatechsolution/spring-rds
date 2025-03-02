package com.yuga.spring_rds.controller;

import com.yuga.spring_rds.model.request.BroadcastMessageTemplateRequest;
import com.yuga.spring_rds.model.response.BroadcastMessageTemplateResponse;
import com.yuga.spring_rds.service.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppController {

  @Autowired private WhatsAppService whatsappService;

  @PostMapping("/broadcast")
  public ResponseEntity<BroadcastMessageTemplateResponse> broadcastMessage(
      @RequestBody BroadcastMessageTemplateRequest request) {
    BroadcastMessageTemplateResponse response =
        whatsappService.broadcastWhatsAppMessageTemplate(request);
    return ResponseEntity.ok(response);
  }
}
