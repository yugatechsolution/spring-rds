package com.yuga.spring_rds.domain.whatsapp.messageRequestType;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextMessageRequest {
  String body;
  boolean previewUrl = true;
}
