package com.yuga.spring_rds.dto;

import com.yuga.spring_rds.domain.whatsapp.messageRequestType.*;
import com.yuga.spring_rds.model.api.request.WhatsAppMessageRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChatbotMessageDTO extends WhatsAppMessageRequest {
  private String triggerText;
}
