package com.yuga.spring_rds.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.connector.WhatsAppConnector;
import com.yuga.spring_rds.domain.whatsapp.ChatbotMessage;
import com.yuga.spring_rds.model.whatsapp.request.WhatsAppMessageRequestModel;
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

  public void sendMessage(ChatbotMessage chatbotMessage, String recipientPhoneNumber) {
    WhatsAppMessageRequestModel whatsAppMessageRequestModel =
        WhatsAppMessageRequestModel.builder()
            .messagingProduct("whatsapp")
            .recipientType("individual")
            .to(recipientPhoneNumber)
            .type(chatbotMessage.getType())
            .build();
    whatsAppMessageRequestModel.enrich(chatbotMessage, mapper);
    log.info("Sending whatsapp message based on trigger text: {}", whatsAppMessageRequestModel);
    var response = whatsAppConnector.sendWhatsAppMessage(whatsAppMessageRequestModel);
    chatService.saveOutgoingMessage(whatsAppMessageRequestModel, response);
  }
}
