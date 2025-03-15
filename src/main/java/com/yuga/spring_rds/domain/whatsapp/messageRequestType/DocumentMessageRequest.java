package com.yuga.spring_rds.domain.whatsapp.messageRequestType;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMessageRequest {
  String id;
  String link;
  String caption;
  String fileName;
}
