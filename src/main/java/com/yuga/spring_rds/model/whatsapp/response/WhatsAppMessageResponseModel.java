package com.yuga.spring_rds.model.whatsapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
  @JsonProperty("messaging_product")
  private String messagingProduct;

  private List<Message> messages;
  private List<Contact> contacts;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Message {
    private String id;

    @JsonProperty("message_status")
    private String messageStatus;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Contact {
    private String input;

    @JsonProperty("wa_id")
    private String waId;
  }
}
