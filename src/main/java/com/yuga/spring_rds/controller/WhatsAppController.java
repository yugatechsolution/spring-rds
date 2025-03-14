package com.yuga.spring_rds.controller;

import com.yuga.spring_rds.model.api.request.WhatsAppMessageRequest;
import com.yuga.spring_rds.model.api.response.SendMessageResponse;
import com.yuga.spring_rds.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/whatsapp")
public class WhatsAppController {

  @Autowired private ChatService chatService;

  @PostMapping("/send")
  public ResponseEntity<SendMessageResponse> broadcastMessage(
      @RequestBody WhatsAppMessageRequest request) {
    SendMessageResponse response = chatService.sendMessage(request);
    return ResponseEntity.ok(response);
  }
}
