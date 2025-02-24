package com.yuga.spring_rds.model.request;

import com.yuga.spring_rds.model.whatsapp.WhatsAppMessageType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastMessageRequest {
  private WhatsAppMessageType whatsAppMessageType;
  private List<String> phoneNumbers;
  private String templateName;
  private String text;
}
