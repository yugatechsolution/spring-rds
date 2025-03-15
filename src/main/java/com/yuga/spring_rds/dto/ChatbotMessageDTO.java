package com.yuga.spring_rds.dto;

import com.yuga.spring_rds.domain.whatsapp.util.BaseWhatsAppMessageRequest;
import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotMessageDTO {
  private String triggerText;
  List<BaseWhatsAppMessageRequest> whatsAppMessageRequests;
  List<NextMessageMappingDTO> connections;
}
