package com.yuga.spring_rds.controller;

import com.yuga.spring_rds.domain.ChatMessage;
import com.yuga.spring_rds.service.ChatService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

  @Autowired private ChatService chatService;

  @GetMapping("/{waId}")
  public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String waId) {
    return ResponseEntity.ok(chatService.getChatHistory(waId));
  }
}
