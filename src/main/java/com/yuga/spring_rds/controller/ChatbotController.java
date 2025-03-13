package com.yuga.spring_rds.controller;

import com.yuga.spring_rds.dto.ChatbotMessageDTO;
import com.yuga.spring_rds.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

  @Autowired private ChatbotService chatbotService;

  @PostMapping
  public ResponseEntity<ChatbotMessageDTO> getChatHistory(
      @RequestBody ChatbotMessageDTO chatbotMessageDTO) {
    return ResponseEntity.ok(chatbotService.createChatbotFlow(chatbotMessageDTO));
  }
}
