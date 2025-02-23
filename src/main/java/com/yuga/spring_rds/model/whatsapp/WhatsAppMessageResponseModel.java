package com.yuga.spring_rds.model.whatsapp;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppMessageResponseModel {
  private String messagingProduct;
  private List<Message> messages;
  private String contacts;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Message {
    private String id;
  }
}
