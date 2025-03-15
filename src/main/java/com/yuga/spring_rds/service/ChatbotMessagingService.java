package com.yuga.spring_rds.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.connector.WhatsAppConnector;
import com.yuga.spring_rds.domain.ChatMessage;
import com.yuga.spring_rds.domain.ChatMessageMapping;
import com.yuga.spring_rds.domain.whatsapp.ChatbotMessage;
import com.yuga.spring_rds.domain.whatsapp.NextMessageMapping;
import com.yuga.spring_rds.model.whatsapp.request.WhatsAppMessageRequestModel;
import com.yuga.spring_rds.repository.ChatMessageMappingsRepository;
import com.yuga.spring_rds.repository.NextMessageMappingRepository;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatbotMessagingService {

  @Value("${whatsapp.phone.number.id}")
  private String phoneNumberId;

  static ObjectMapper mapper = WhatsAppConnector.mapper;
  @Autowired private WhatsAppConnector whatsAppConnector;
  @Autowired private ChatService chatService;
  @Autowired private ChatMessageMappingsRepository chatMessageMappingsRepository;
  @Autowired private NextMessageMappingRepository nextMessageMappingRepository;

  public void sendMessage(ChatbotMessage chatbotMessage, String recipientPhoneNumber) {
    if (Objects.isNull(chatbotMessage)) return;
    WhatsAppMessageRequestModel whatsAppMessageRequestModel =
        WhatsAppMessageRequestModel.builder()
            .messagingProduct("whatsapp")
            .recipientType("individual")
            .to(recipientPhoneNumber)
            .type(chatbotMessage.getType())
            .build();
    whatsAppMessageRequestModel.enrich(chatbotMessage.getRequest(), mapper);
    log.info("Sending whatsapp message based on trigger text: {}", whatsAppMessageRequestModel);
    var response = whatsAppConnector.sendWhatsAppMessage(whatsAppMessageRequestModel);
    ChatMessage chatMessage =
        chatService.saveOutgoingMessage(whatsAppMessageRequestModel, response);
    ChatMessageMapping chatMessageMapping =
        ChatMessageMapping.builder()
            .chatMessage(chatMessage)
            .chatbotMessage(chatbotMessage)
            .build();
    log.info("Saving ChatMessageMapping: {}", chatMessageMapping);
    chatMessageMappingsRepository.save(chatMessageMapping);
  }

  public ChatbotMessage getChatbotMessageByMessageId(String messageId) {
    log.info(
        "Finding Chatbot Message By MessageId for {} using ChatbotMessageIdMapping", messageId);
    return chatMessageMappingsRepository
        .findByMessageId(messageId)
        .map(ChatMessageMapping::getChatbotMessage)
        .orElse(null);
  }

  public ChatbotMessage getNextChatbotMessage(
      ChatbotMessage parentChatbotMessage, String actionTrigger) {
    log.info(
        "Finding next chatbot message for chatbotTemplateId={}, actionTrigger={}",
        parentChatbotMessage.getId(),
        actionTrigger);
    return nextMessageMappingRepository
        .findByParentMessageAndActionTrigger(parentChatbotMessage, actionTrigger)
        .map(NextMessageMapping::getNextMessage)
        .orElse(null);
  }
}
