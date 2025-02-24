package com.yuga.spring_rds.controller;

import com.yuga.spring_rds.model.request.BroadcastMessageRequest;
import com.yuga.spring_rds.model.whatsapp.response.WhatsAppMessageResponseModel;
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
  public ResponseEntity<WhatsAppMessageResponseModel> broadcastMessage(
      @RequestBody BroadcastMessageRequest request) {
    WhatsAppMessageResponseModel response = whatsappService.sendWhatsAppMessage(request);
    return ResponseEntity.ok(response);
  }
}
