package com.yuga.spring_rds.model.whatsapp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WhatsAppMessageRequestModel {
  private String messagingProduct = "whatsapp";
  private String recipientType = "individual";
  private String to;
  private String type = "text";
  private MessageText text;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class MessageText {
    private String body;
  }
}
