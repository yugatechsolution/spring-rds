package com.yuga.spring_rds.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.connector.WhatsAppConnector;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MessageRequestConverter implements AttributeConverter<JsonNode, String> {

  private final ObjectMapper objectMapper = WhatsAppConnector.mapper;

  @Override
  public String convertToDatabaseColumn(JsonNode attribute) {
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to convert MessageRequest to JSON", e);
    }
  }

  @Override
  public JsonNode convertToEntityAttribute(String dbData) {
    try {
      if (dbData == null) return null;
      // Use Jackson to figure out the type based on `type` field
      return objectMapper.readValue(dbData, JsonNode.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to convert JSON to JsonNode", e);
    }
  }
}
