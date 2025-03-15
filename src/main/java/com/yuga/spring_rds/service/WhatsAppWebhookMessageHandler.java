package com.yuga.spring_rds.service;

import com.yuga.spring_rds.domain.whatsapp.ChatbotMessage;
import com.yuga.spring_rds.domain.whatsapp.WhatsAppMessageType;
import com.yuga.spring_rds.model.api.request.WhatsAppWebhookRequest;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhatsAppWebhookMessageHandler {
  @Autowired private ChatService chatService;
  @Autowired private ChatbotService chatbotService;
  @Autowired private ChatbotMessagingService chatbotMessagingService;

  public void handleMessage(String phoneNumberId, WhatsAppWebhookRequest.Message message) {
    try {
      chatService.saveIncomingMessage(phoneNumberId, message);
      if (message.getType().equals(WhatsAppMessageType.text)) {
        ChatbotMessage chatbotMessage =
            chatbotService.getFirstMessageOfFlowIfExists(message.getText().getBody());
        if (Objects.nonNull(chatbotMessage))
          chatbotMessagingService.sendMessage(chatbotMessage, message.getFrom());
      }
    } catch (Exception e) {
      log.error("Failed to save message for phoneNumberId={}", phoneNumberId, e);
    }
  }
}
