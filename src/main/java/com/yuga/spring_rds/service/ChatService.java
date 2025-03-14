package com.yuga.spring_rds.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.connector.WhatsAppConnector;
import com.yuga.spring_rds.domain.ChatMessage;
import com.yuga.spring_rds.domain.WhatsAppContactId;
import com.yuga.spring_rds.model.api.request.WhatsAppMessageRequest;
import com.yuga.spring_rds.model.api.request.WhatsAppWebhookRequest;
import com.yuga.spring_rds.model.api.response.SendMessageResponse;
import com.yuga.spring_rds.model.whatsapp.reply.InteractiveReply;
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

  public SendMessageResponse sendMessage(WhatsAppMessageRequest request) {
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
                            this.saveOutgoingMessage(waReqModel, response);
                            return response;
                          } catch (Exception e) {
                            return e.getMessage();
                          }
                        })))
        .build();
  }

  public void saveIncomingMessage(String phoneNumberId, WhatsAppWebhookRequest.Message message) {
    ChatMessage.ChatMessageBuilder chatMessageBuilder =
        ChatMessage.builder()
            .phoneNumberId(phoneNumberId)
            .waId(message.getFrom())
            .messageId(message.getId())
            .timestamp(System.currentTimeMillis() / 1000)
            .direction(ChatMessage.Direction.INCOMING);
    switch (message.getType()) {
      case text ->
          chatMessageBuilder
              .messageBody(message.getText().getBody())
              .messageType(ChatMessage.MessageType.TEXT)
              .build();
      case interactive -> {
        if (message.getInteractive().getType().equals(InteractiveReply.Type.list_reply)) {
          chatMessageBuilder
              .messageBody(message.getInteractive().getListReply().getTitle())
              .metadata(convertToJson(message.getInteractive().getListReply()))
              .messageType(ChatMessage.MessageType.LIST)
              .build();
        } else if (message.getInteractive().getType().equals(InteractiveReply.Type.button_reply)) {
          chatMessageBuilder
              .messageBody(message.getInteractive().getButtonReply().getTitle())
              .metadata(convertToJson(message.getInteractive().getButtonReply()))
              .messageType(ChatMessage.MessageType.BUTTON)
              .build();
        } else {
          log.info("Interactive type not supported, id={}", message.getId());
          return;
        }
      }
      case image ->
          chatMessageBuilder
              .messageBody("Image received")
              .metadata(convertToJson(message.getImage()))
              .messageType(ChatMessage.MessageType.IMAGE)
              .build();

      case audio ->
          chatMessageBuilder
              .messageBody("Audio received")
              .metadata(convertToJson(message.getAudio()))
              .messageType(ChatMessage.MessageType.AUDIO)
              .build();

      case video ->
          chatMessageBuilder
              .messageBody("Video received")
              .metadata(convertToJson(message.getVideo()))
              .messageType(ChatMessage.MessageType.VIDEO)
              .build();

      case document ->
          chatMessageBuilder
              .messageBody("Document received")
              .metadata(convertToJson(message.getDocument()))
              .messageType(ChatMessage.MessageType.DOCUMENT)
              .build();

      default -> {
        log.info("Unsupported message type: {}", message.getType());
        return;
      }
    }
    ChatMessage chatMessage = chatMessageBuilder.build();
    this.saveChatMessage(chatMessage);
  }

  public void saveOutgoingMessage(
      WhatsAppMessageRequestModel requestModel, WhatsAppMessageResponseModel responseModel) {

    ChatMessage.ChatMessageBuilder builder =
        ChatMessage.builder()
            .phoneNumberId(phoneNumberId)
            .waId(responseModel.getContacts().getFirst().getWaId())
            .messageId(responseModel.getMessages().getFirst().getId())
            .timestamp(System.currentTimeMillis() / 1000)
            .direction(ChatMessage.Direction.OUTGOING);

    switch (requestModel.getType()) {
      case text ->
          builder
              .messageType(ChatMessage.MessageType.TEXT)
              .messageBody(requestModel.getText().getBody());
      default -> {
        log.info("Unsupported message type: {}", requestModel.getType());
        return;
      }
    }
    this.saveChatMessage(builder.build());
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

  private String convertToJson(Object data) {
    try {
      return new ObjectMapper().writeValueAsString(data);
    } catch (JsonProcessingException e) {
      log.error("Failed to convert object to JSON", e);
      return null;
    }
  }
}
