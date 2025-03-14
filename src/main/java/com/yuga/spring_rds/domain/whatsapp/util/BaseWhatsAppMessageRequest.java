package com.yuga.spring_rds.domain.whatsapp.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
      property = "type",
      visible = true)
  @JsonSubTypes({
    @JsonSubTypes.Type(value = TextMessageRequest.class, name = "text"),
    @JsonSubTypes.Type(value = InteractiveMessageRequest.class, name = "interactive"),
    @JsonSubTypes.Type(value = ImageMessageRequest.class, name = "image"),
    @JsonSubTypes.Type(value = DocumentMessageRequest.class, name = "document"),
    @JsonSubTypes.Type(value = VideoMessageRequest.class, name = "video")
  })
  private MessageRequest request;
}
