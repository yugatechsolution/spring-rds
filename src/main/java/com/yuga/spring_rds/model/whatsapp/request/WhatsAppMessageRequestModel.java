package com.yuga.spring_rds.model.whatsapp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppMessageRequestModel {

  @JsonProperty("messaging_product")
  private String messagingProduct;

  @JsonProperty("recipient_type")
  private String recipientType;

  private String to;
  private String type;
  private Template template;
  private Text text;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Template {
    private String name;
    private Language language;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Text {
    private String body;

    @JsonProperty("preview_url")
    private boolean previewUrl;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Language {
    private String code;
  }
}
