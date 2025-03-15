package com.yuga.spring_rds.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.connector.WhatsAppConnector;
import com.yuga.spring_rds.domain.User;
import com.yuga.spring_rds.domain.whatsapp.*;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.*;
import com.yuga.spring_rds.domain.whatsapp.util.BaseWhatsAppMessageRequest;
import com.yuga.spring_rds.dto.ChatbotFlowDTO;
import com.yuga.spring_rds.dto.NextMessageMappingDTO;
import com.yuga.spring_rds.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatbotService {

  private final ObjectMapper mapper = WhatsAppConnector.mapper;

  @Autowired private ChatbotTriggerRepository chatbotTriggerRepository;
  @Autowired private ChatbotMessageRepository chatbotMessageRepository;
  @Autowired private NextMessageMappingRepository nextMessageMappingRepository;

  public ChatbotMessage getFirstMessageOfFlowIfExists(String triggerText) {
    return chatbotTriggerRepository.findStartingMessage(triggerText).orElse(null);
  }

  public ChatbotFlowDTO getFullFlow(String triggerText) {
    ChatbotTrigger trigger =
        chatbotTriggerRepository
            .findFullFlowByTriggerText(triggerText)
            .orElseThrow(() -> new EntityNotFoundException("Invalid triggerText"));

    ChatbotMessage startingMessage = trigger.getStartingMessage();
    log.info("Starting message for trigger '{}' -> ID: {}", triggerText, startingMessage.getId());

    List<BaseWhatsAppMessageRequest> messageRequests = new ArrayList<>();
    List<NextMessageMappingDTO> connections = new ArrayList<>();
    Map<Long, Integer> messageIndexMap = new HashMap<>();

    // Flatten the message tree
    flattenFlow(startingMessage, messageRequests, connections, messageIndexMap);

    return new ChatbotFlowDTO(triggerText, messageRequests, connections);
  }

  private void flattenFlow(
      ChatbotMessage chatbotMessage,
      List<BaseWhatsAppMessageRequest> messageRequests,
      List<NextMessageMappingDTO> connections,
      Map<Long, Integer> messageIndexMap) {

    if (messageIndexMap.containsKey(chatbotMessage.getId())) {
      return;
    }

    int currentIndex = messageRequests.size();
    messageIndexMap.put(chatbotMessage.getId(), currentIndex);

    messageRequests.add(convertToBaseWhatsAppMessageRequest(chatbotMessage));

    for (NextMessageMapping mapping : chatbotMessage.getNextMessages()) {
      ChatbotMessage nextMessage = mapping.getNextMessage();
      flattenFlow(nextMessage, messageRequests, connections, messageIndexMap);
      int nextIndex = messageIndexMap.get(nextMessage.getId());
      connections.add(
          new NextMessageMappingDTO(currentIndex, nextIndex, mapping.getActionTrigger()));
    }
  }

  private BaseWhatsAppMessageRequest convertToBaseWhatsAppMessageRequest(ChatbotMessage message) {
    log.info("Converting message ID={} to BaseWhatsAppMessageRequest", message.getId());
    BaseWhatsAppMessageRequest.BaseWhatsAppMessageRequestBuilder builder =
        BaseWhatsAppMessageRequest.builder();
    return switch (message.getType()) {
      case document -> null;
      case image -> null;
      case text ->
          builder
              .type(WhatsAppMessageType.text)
              .text(mapper.convertValue(message.getRequest(), TextMessageRequest.class))
              .build();
      case interactive ->
          builder
              .type(WhatsAppMessageType.interactive)
              .interactive(
                  mapper.convertValue(message.getRequest(), InteractiveMessageRequest.class))
              .build();
      case video -> null;
      case audio -> null;
    };
  }

  /** Create a new chatbot flow. */
  @Transactional
  public ChatbotFlowDTO createChatbotFlow(ChatbotFlowDTO chatbotFlowDTO, User user) {
    log.info("Creating chatbot flow for triggerText={}", chatbotFlowDTO.getTriggerText());

    chatbotTriggerRepository
        .findByTriggerText(chatbotFlowDTO.getTriggerText())
        .ifPresent(
            chatbotTrigger -> {
              throw new RuntimeException("Chatbot Flow already exists");
            });
    Map<Integer, ChatbotMessage> indexToMessageMapping = new HashMap<>();

    IntStream.range(0, chatbotFlowDTO.getWhatsAppMessageRequests().size())
        .forEach(
            index -> {
              BaseWhatsAppMessageRequest msgReq =
                  chatbotFlowDTO.getWhatsAppMessageRequests().get(index);
              ChatbotMessage chatbotMessage =
                  ChatbotMessage.builder()
                      .user(user)
                      .type(msgReq.getType())
                      .request(getMessageRequest(msgReq))
                      .build();
              chatbotMessageRepository.save(chatbotMessage);
              indexToMessageMapping.put(index, chatbotMessage);
            });

    ChatbotTrigger chatbotTrigger =
        ChatbotTrigger.builder()
            .startingMessage(indexToMessageMapping.get(0))
            .triggerText(chatbotFlowDTO.getTriggerText())
            .build();
    chatbotTriggerRepository.save(chatbotTrigger);

    // Link the trigger to all messages
    indexToMessageMapping
        .values()
        .forEach(
            message -> {
              message.setTrigger(chatbotTrigger);
              chatbotMessageRepository.save(message);
            });

    chatbotFlowDTO
        .getConnections()
        .forEach(
            nextMessageMappingDTO -> {
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
        "Successfully created chatbot flow for triggerText={}", chatbotFlowDTO.getTriggerText());
    return chatbotFlowDTO;
  }

  private JsonNode getMessageRequest(BaseWhatsAppMessageRequest request) {
    return switch (request.getType()) {
      case text -> mapper.convertValue(request.getText(), JsonNode.class);
      case interactive -> mapper.convertValue(request.getInteractive(), JsonNode.class);
      case video -> mapper.convertValue(request.getVideo(), JsonNode.class);
      default -> null;
    };
  }

  @Transactional
  public void deleteFullChatbotFlow(String triggerText) {
    log.info("Starting deletion for chatbot flow with triggerText={}", triggerText);

    ChatbotTrigger trigger =
        chatbotTriggerRepository
            .findByTriggerText(triggerText)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "ChatbotTrigger not found for triggerText: " + triggerText));

    chatbotTriggerRepository.delete(trigger); // Cascade should handle the rest

    log.info("Deleted ChatbotTrigger with ID={}", trigger.getId());
  }
}
