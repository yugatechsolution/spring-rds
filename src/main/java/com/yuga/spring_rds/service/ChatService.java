package com.yuga.spring_rds.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.connector.WhatsAppConnector;
import com.yuga.spring_rds.domain.ChatMessage;
import com.yuga.spring_rds.domain.WhatsAppContactId;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.interactive.InteractiveMessageType;
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

  static ObjectMapper mapper = WhatsAppConnector.mapper;
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
              .messageType(ChatMessage.MessageType.TEXT);
      case interactive -> {
        if (message.getInteractive().getType().equals(InteractiveReply.Type.list_reply)) {
          chatMessageBuilder
              .messageBody(message.getInteractive().getListReply().getTitle())
              .metadata(convertToJson(message.getInteractive().getListReply()))
              .messageType(ChatMessage.MessageType.LIST);
        } else if (message.getInteractive().getType().equals(InteractiveReply.Type.button_reply)) {
          chatMessageBuilder
              .messageBody(message.getInteractive().getButtonReply().getTitle())
              .metadata(convertToJson(message.getInteractive().getButtonReply()))
              .messageType(ChatMessage.MessageType.BUTTON);
        } else {
          log.info("Unsupported interactive type: {}", message.getInteractive().getType());
          return;
        }
      }
      case image ->
          chatMessageBuilder
              .messageBody("Image received")
              .metadata(convertToJson(message.getImage()))
              .messageType(ChatMessage.MessageType.IMAGE);
      case audio ->
          chatMessageBuilder
              .messageBody("Audio received")
              .metadata(convertToJson(message.getAudio()))
              .messageType(ChatMessage.MessageType.AUDIO);
      case video ->
          chatMessageBuilder
              .messageBody("Video received")
              .metadata(convertToJson(message.getVideo()))
              .messageType(ChatMessage.MessageType.VIDEO);
      case document ->
          chatMessageBuilder
              .messageBody("Document received")
              .metadata(convertToJson(message.getDocument()))
              .messageType(ChatMessage.MessageType.DOCUMENT);
      default -> {
        log.info("Unsupported message type: {}", message.getType());
        return;
      }
    }

    ChatMessage chatMessage = chatMessageBuilder.build();
    this.saveChatMessage(chatMessage);
  }

  public ChatMessage saveOutgoingMessage(
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
      case interactive -> {
        try {
          builder.metadata(mapper.writeValueAsString(requestModel.getInteractive()));
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
        InteractiveMessageType interactiveMessageType = requestModel.getInteractive().getType();
        switch (interactiveMessageType) {
          case list -> builder.messageType(ChatMessage.MessageType.LIST);
          case button -> builder.messageType(ChatMessage.MessageType.BUTTON);
        }
      }
      default -> {
        log.info("Unsupported message type: {}", requestModel.getType());
        return null;
      }
    }

    builder.status(ChatMessage.Status.SENT.name());
    builder.statusTimestamp(System.currentTimeMillis() / 1000);
    return this.saveChatMessage(builder.build());
  }

  public void updateMessageStatus(String messageId, String status) {
    ChatMessage chatMessage = chatMessageRepository.findByMessageId(messageId);
    if (chatMessage != null) {
      chatMessage.setStatus(status);
      chatMessage.setStatusTimestamp(System.currentTimeMillis() / 1000);
      chatMessageRepository.save(chatMessage);
      log.info("Updated message status to {} for messageId={}", status, messageId);
    } else {
      log.warn("Message with ID {} not found for status update", messageId);
    }
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
