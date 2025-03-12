package com.yuga.spring_rds.domain.whatsapp.messageRequestType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoMessageRequest implements MessageRequest {
  String id;
  String link;
  String caption;
}
