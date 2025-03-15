package com.yuga.spring_rds.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.connector.WhatsAppConnector;
import com.yuga.spring_rds.domain.Contact;
import com.yuga.spring_rds.domain.ContactId;
import com.yuga.spring_rds.domain.User;
import com.yuga.spring_rds.dto.ChatbotFlowDTO;
import com.yuga.spring_rds.dto.ContactDTO;
import com.yuga.spring_rds.model.api.request.WhatsAppWebhookRequest;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestUtil {
  private static final ObjectMapper objectMapper = WhatsAppConnector.mapper;

  public static final String USERNAME = "root";
  public static final String EMAIL = "root@gmail.com";
  public static final String PASSWORD = "root";
  public static final String PHONE_NUMBER = "1234567890";

  public static User getUser() {
    return User.builder().username(USERNAME).email(EMAIL).password(PASSWORD).build();
  }

  public static Contact getContact() {
    User testUser = getUser();
    return new Contact(new ContactId(testUser.getId(), PHONE_NUMBER), "John Doe", testUser);
  }

  public static Contact getContact(User user) {
    return new Contact(new ContactId(user.getId(), PHONE_NUMBER), "John Doe", user);
  }

  public static ContactDTO getContactDTO() {
    return ContactDTO.builder().phoneNumber(PHONE_NUMBER).name("John Doe").build();
  }

  public static ChatbotFlowDTO getChatbotMessage() {
    return readfromFile(
        "D:\\Development\\spring-rds\\src\\test\\resources\\chatbot_flow.json",
        ChatbotFlowDTO.class,
        new ObjectMapper());
  }

  public static WhatsAppWebhookRequest getWhatsAppWebhookRequest() {
    return readfromFile(
        "D:\\Development\\spring-rds\\src\\test\\resources\\whatsapp_webhook_text_msg.json",
        WhatsAppWebhookRequest.class,
        objectMapper);
  }

  public static <T> T readfromFile(String fileLocation, Class<T> clazz, ObjectMapper mapper) {
    try {
      File file = new File(fileLocation);
      if (!file.exists()) {
        throw new IOException("File not found");
      }
      return mapper.readValue(file, clazz);
    } catch (Exception e) {
      log.error("Error parsing chatbot message JSON from file: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to parse chatbot message from file", e);
    }
  }
}
