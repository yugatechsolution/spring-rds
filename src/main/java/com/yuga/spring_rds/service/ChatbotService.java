package com.yuga.spring_rds.service;

import com.yuga.spring_rds.advice.RequestContext;
import com.yuga.spring_rds.domain.whatsapp.ChatbotMessage;
import com.yuga.spring_rds.domain.whatsapp.ChatbotTrigger;
import com.yuga.spring_rds.domain.whatsapp.NextMessageMapping;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.MessageRequest;
import com.yuga.spring_rds.dto.ChatbotMessageDTO;
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

  public ChatbotMessageDTO createChatbotFlow(ChatbotMessageDTO chatbotMessageDTO) {
    ChatbotMessage chatbotMessage =
        ChatbotMessage.builder()
            .user(RequestContext.getUser())
            .type(chatbotMessageDTO.getType())
            .request(getMessageRequest(chatbotMessageDTO))
            .build();
    chatbotMessageRepository.save(chatbotMessage);
    ChatbotTrigger chatbotTrigger =
        ChatbotTrigger.builder()
            .chatbotMessage(chatbotMessage)
            .triggerText(chatbotMessageDTO.getTriggerText())
            .build();
    chatbotTriggerRepository.save(chatbotTrigger);
    chatbotMessageDTO.setId(chatbotMessage.getId());
    return chatbotMessageDTO;
  }

  private MessageRequest getMessageRequest(ChatbotMessageDTO chatbotMessageDTO) {
    return switch (chatbotMessageDTO.getType()) {
      case text -> chatbotMessageDTO.getText();
      case video -> chatbotMessageDTO.getVideo();
      default -> null;
    };
  }
}
