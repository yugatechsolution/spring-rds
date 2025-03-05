package com.yuga.spring_rds.service;

import com.yuga.spring_rds.domain.ChatMessage;
import com.yuga.spring_rds.model.api.request.WhatsAppWebhookRequest;
import com.yuga.spring_rds.model.whatsapp.request.WhatsAppMessageRequestModel;
import com.yuga.spring_rds.model.whatsapp.response.WhatsAppMessageResponseModel;
import com.yuga.spring_rds.repository.ChatMessageRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

  @Value("${whatsapp.phone.number.id}")
  private String phoneNumberId;

  @Autowired private ChatMessageRepository chatMessageRepository;

  public void saveMessage(String phoneNumberId, WhatsAppWebhookRequest.Message message) {
    ChatMessage chatMessage =
        ChatMessage.builder()
            .phoneNumberId(phoneNumberId)
            .waId(message.getFrom())
            .messageId(message.getId())
            .timestamp(System.currentTimeMillis() / 1000)
            .messageBody(message.getText().getBody())
            .direction(ChatMessage.Direction.INCOMING)
            .messageType(ChatMessage.MessageType.TEXT)
            .build();
    chatMessageRepository.save(chatMessage);
  }

  public void saveMessage(
      WhatsAppMessageRequestModel requestModel, WhatsAppMessageResponseModel responseModel) {
    ChatMessage chatMessage =
        ChatMessage.builder()
            .phoneNumberId(phoneNumberId)
            .waId(responseModel.getContacts().getFirst().getWaId())
            .messageId(responseModel.getMessages().getFirst().getId())
            .timestamp(System.currentTimeMillis() / 1000)
            .messageBody(requestModel.getText().getBody())
            .direction(ChatMessage.Direction.OUTGOING)
            .messageType(ChatMessage.MessageType.TEXT)
            .build();
    chatMessageRepository.save(chatMessage);
  }

  public List<ChatMessage> getChatHistory(String waId) {
    return chatMessageRepository.findByWaIdOrderByTimestampAsc(waId);
  }
}
