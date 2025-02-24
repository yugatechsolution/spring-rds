package com.yuga.spring_rds.model.whatsapp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WhatsAppTextMessageRequestModel {
  @JsonProperty("messaging_product")
  private String messagingProduct = "whatsapp";

  @JsonProperty("recipient_type")
  private String recipientType = "individual";

  private String to;
  private String type = "text";
  private Text text;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Text {
    private String body;
  }
}
