package com.yuga.spring_rds.domain.whatsapp.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.domain.whatsapp.WhatsAppMessageType;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseWhatsAppMessageRequest {

  private WhatsAppMessageType type;
  private TextMessageRequest text;
  private VideoMessageRequest video;
  private InteractiveMessageRequest interactive;

  public <T> void enrich(T requestBody, ObjectMapper mapper) {
    switch (type) {
      case text -> this.text = mapper.convertValue(requestBody, TextMessageRequest.class);
      case interactive ->
          this.interactive = mapper.convertValue(requestBody, InteractiveMessageRequest.class);
    }
  }
}
