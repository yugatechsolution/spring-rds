package com.yuga.spring_rds.service;

import com.yuga.spring_rds.connector.WhatsAppConnector;
import com.yuga.spring_rds.domain.ChatMessage;
import com.yuga.spring_rds.domain.WhatsAppContactId;
import com.yuga.spring_rds.model.api.request.SendMessageRequest;
import com.yuga.spring_rds.model.api.request.WhatsAppWebhookRequest;
import com.yuga.spring_rds.model.api.response.SendMessageResponse;
import com.yuga.spring_rds.model.whatsapp.request.WhatsAppMessageRequestModel;
import com.yuga.spring_rds.model.whatsapp.response.WhatsAppMessageResponseModel;
import com.yuga.spring_rds.repository.ChatMessageRepository;
import com.yuga.spring_rds.util.WhatsAppUtil;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatService {

  @Value("${whatsapp.phone.number.id}")
  private String phoneNumberId;

  @Autowired private WhatsAppConnector whatsAppConnector;
  @Autowired private WhatsAppContactService whatsAppContactService;
  @Autowired private ChatMessageRepository chatMessageRepository;

  public SendMessageResponse sendMessage(SendMessageRequest request) {
    return SendMessageResponse.builder()
        .responseDetails(
            IntStream.range(0, request.getPhoneNumbers().size())
                .mapToObj(
                    index -> WhatsAppUtil.buildWhatsAppTemplateMessageRequestModel(request, index))
                .filter(Objects::nonNull)
                .collect(
                    Collectors.toMap(
                        WhatsAppMessageRequestModel::getTo,
                        waReqModel -> {
                          try {
                            var response = whatsAppConnector.sendWhatsAppMessage(waReqModel);
                            this.saveMessage(waReqModel, response);
                            return response;
                          } catch (Exception e) {
                            return e.getMessage();
                          }
                        })))
        .build();
  }

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
    this.saveChatMessage(chatMessage);
  }

  public ChatMessage saveMessage(
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
    return this.saveChatMessage(chatMessage);
  }

  public List<ChatMessage> getChatHistory(String waId) {
    return chatMessageRepository.findByWaIdAndPhoneNumberIdOrderByTimestampAsc(waId, phoneNumberId);
  }

  private ChatMessage saveChatMessage(ChatMessage chatMessage) {
    log.info("Saving chat message: {}", chatMessage);
    return chatMessageRepository.save(chatMessage);
  }

  @Transactional
  public void deleteChatHistory(String waId) {
    log.info("Deleting chats for waId={}", waId);
    chatMessageRepository.deleteMessagesByWaIdAndPhoneNumberId(waId, phoneNumberId);
    whatsAppContactService.deleteWhatsAppContact(
        WhatsAppContactId.builder().phoneNumberId(phoneNumberId).waId(waId).build());
  }
}
