package com.yuga.spring_rds.domain.whatsapp.messageRequestType;

import com.yuga.spring_rds.domain.whatsapp.WhatsAppMessageType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoMessageRequest {
  String id;
  String link;
  String caption;

  @Builder.Default
  private final WhatsAppMessageType whatsAppMessageType = WhatsAppMessageType.video;
}
