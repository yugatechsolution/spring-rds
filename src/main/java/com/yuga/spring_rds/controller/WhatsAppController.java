package com.yuga.spring_rds.controller;

import com.yuga.spring_rds.model.api.request.SendMessageRequest;
import com.yuga.spring_rds.model.api.response.SendMessageResponse;
import com.yuga.spring_rds.service.SendMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/whatsapp")
public class WhatsAppController {

  @Autowired private SendMessageService whatsappService;

  @PostMapping("/message")
  public ResponseEntity<SendMessageResponse> broadcastMessage(
      @RequestBody SendMessageRequest request) {
    SendMessageResponse response = whatsappService.sendMessage(request);
    return ResponseEntity.ok(response);
  }
}
