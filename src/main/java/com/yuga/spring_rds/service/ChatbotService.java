package com.yuga.spring_rds.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.connector.WhatsAppConnector;
import com.yuga.spring_rds.domain.User;
import com.yuga.spring_rds.domain.whatsapp.ChatbotMessage;
import com.yuga.spring_rds.domain.whatsapp.ChatbotTrigger;
import com.yuga.spring_rds.domain.whatsapp.NextMessageMapping;
import com.yuga.spring_rds.domain.whatsapp.util.BaseWhatsAppMessageRequest;
import com.yuga.spring_rds.dto.ChatbotMessageDTO;
import com.yuga.spring_rds.repository.ChatbotMessageRepository;
import com.yuga.spring_rds.repository.ChatbotTriggerRepository;
import com.yuga.spring_rds.repository.NextMessageMappingRepository;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatbotService {

  public ObjectMapper mapper = WhatsAppConnector.mapper;

  @Autowired private ChatbotTriggerRepository chatbotTriggerRepository;
  @Autowired private ChatbotMessageRepository chatbotMessageRepository;
  @Autowired private NextMessageMappingRepository nextMessageMappingRepository;

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

  @Transactional
  public ChatbotMessageDTO createChatbotFlow(ChatbotMessageDTO chatbotMessageDTO, User user) {
    Map<Integer, ChatbotMessage> indexToMessageMapping = new HashMap<>();
    IntStream.range(0, chatbotMessageDTO.getWhatsAppMessageRequests().size())
        .forEach(
            index -> {
              log.info("Processing and saving chatbot message request for index={}", index);
              BaseWhatsAppMessageRequest msgReq =
                  chatbotMessageDTO.getWhatsAppMessageRequests().get(index);
              ChatbotMessage chatbotMessage =
                  ChatbotMessage.builder()
                      .user(user)
                      .type(msgReq.getType())
                      .request(getMessageRequest(msgReq))
                      .build();
              chatbotMessageRepository.save(chatbotMessage);
              indexToMessageMapping.put(index, chatbotMessage);
            });

    chatbotMessageDTO
        .getConnections()
        .forEach(
            nextMessageMappingDTO -> {
              log.info(
                  "Processing and saving NextMessageMapping for request={}", nextMessageMappingDTO);
              NextMessageMapping nextMessageMapping =
                  NextMessageMapping.builder()
                      .parentMessage(
                          indexToMessageMapping.get(nextMessageMappingDTO.getParentMessageIndex()))
                      .nextMessage(
                          indexToMessageMapping.get(nextMessageMappingDTO.getNextMessageIndex()))
                      .actionTrigger(nextMessageMappingDTO.getActionTrigger())
                      .build();
              nextMessageMappingRepository.save(nextMessageMapping);
            });

    log.info(
        "Processing and saving Chatbot trigger for triggerText={}",
        chatbotMessageDTO.getTriggerText());
    ChatbotTrigger chatbotTrigger =
        ChatbotTrigger.builder()
            .chatbotMessage(indexToMessageMapping.get(0))
            .triggerText(chatbotMessageDTO.getTriggerText())
            .build();
    chatbotTriggerRepository.save(chatbotTrigger);
    return chatbotMessageDTO;
  }

  private JsonNode getMessageRequest(BaseWhatsAppMessageRequest baseWhatsAppMessageRequest) {
    return switch (baseWhatsAppMessageRequest.getType()) {
      case text -> mapper.convertValue(baseWhatsAppMessageRequest.getText(), JsonNode.class);
      case interactive ->
          mapper.convertValue(baseWhatsAppMessageRequest.getInteractive(), JsonNode.class);
      case video -> mapper.convertValue(baseWhatsAppMessageRequest.getVideo(), JsonNode.class);
      default -> null;
    };
  }
}
