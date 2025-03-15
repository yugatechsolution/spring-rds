package com.yuga.spring_rds.controller;

import com.yuga.spring_rds.advice.RequestContext;
import com.yuga.spring_rds.dto.ChatbotFlowDTO;
import com.yuga.spring_rds.service.ChatbotService;
import com.yuga.spring_rds.util.JwtSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@JwtSecured
public class ChatbotController {

  @Autowired private ChatbotService chatbotService;

  @GetMapping
  public ResponseEntity<ChatbotFlowDTO> getFullChatbotFlow(@RequestParam String triggerText) {
    return ResponseEntity.ok(chatbotService.getFullFlow(triggerText));
  }

  @PostMapping
  public ResponseEntity<ChatbotFlowDTO> createChatbotFlow(
      @RequestBody ChatbotFlowDTO chatbotFlowDTO) {
    return ResponseEntity.ok(
        chatbotService.createChatbotFlow(chatbotFlowDTO, RequestContext.getUser()));
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteChatbotFlow(@RequestParam String triggerText) {
    chatbotService.deleteFullChatbotFlow(triggerText);
    return ResponseEntity.noContent().build(); // 204 No Content
  }
}
