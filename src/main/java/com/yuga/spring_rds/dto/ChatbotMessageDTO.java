package com.yuga.spring_rds.dto;

import com.yuga.spring_rds.domain.whatsapp.WhatsAppMessageType;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotMessageDTO {
  private Long id;
  private String phoneNumber;
  private WhatsAppMessageType type;
  private MessageRequest request;
  private String triggerText;
}
