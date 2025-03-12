package com.yuga.spring_rds.domain.whatsapp.messageRequestType;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.interactive.InteractiveCtaUrlMessageRequest;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.interactive.InteractiveListMessageRequest;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.interactive.InteractiveMessageType;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.interactive.InteractiveReplyButtonsMessageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = InteractiveReplyButtonsMessageRequest.class, name = "BUTTON"),
  @JsonSubTypes.Type(value = InteractiveListMessageRequest.class, name = "LIST"),
  @JsonSubTypes.Type(value = InteractiveCtaUrlMessageRequest.class, name = "CTA_URL")
})
public class InteractiveMessageRequest implements MessageRequest {
  InteractiveMessageType type;
}
