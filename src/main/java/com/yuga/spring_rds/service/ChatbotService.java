package com.yuga.spring_rds.service;

import com.yuga.spring_rds.domain.whatsapp.ChatbotMessage;
import com.yuga.spring_rds.domain.whatsapp.ChatbotTrigger;
import com.yuga.spring_rds.domain.whatsapp.NextMessageMapping;
import com.yuga.spring_rds.repository.ChatbotMessageRepository;
import com.yuga.spring_rds.repository.ChatbotTriggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatbotService {

  @Autowired private ChatbotTriggerRepository chatbotTriggerRepository;
  @Autowired private ChatbotMessageRepository chatbotMessageRepository;

  public ChatbotMessage getFullFlowForTrigger(String triggerText) {
    ChatbotTrigger trigger =
        chatbotTriggerRepository
            .findByTriggerText(triggerText)
            .orElseThrow(() -> new IllegalArgumentException("Trigger not found"));

    // Load entire message tree using @EntityGraph
    return chatbotMessageRepository
        .findById(trigger.getChatbotMessage().getId())
        .orElseThrow(() -> new IllegalArgumentException("Message not found"));
  }

  public void printMessageFlow(ChatbotMessage message, int level) {
    String indent = "  ".repeat(level);
    System.out.println(indent + "Message ID: " + message.getId());
    System.out.println(indent + "Message Type: " + message.getType());
    System.out.println(indent + "Message Body: " + message.getRequest());

    for (NextMessageMapping next : message.getNextMessages()) {
      System.out.println(indent + "↳ Trigger: " + next.getActionTrigger());
      printMessageFlow(next.getNextMessage(), level + 1);
    }
  }
}
