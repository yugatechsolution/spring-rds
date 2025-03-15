package com.yuga.spring_rds.domain.whatsapp.messageRequestType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ImageMessageRequest {
  String id;
  String link;
  String caption;
}
