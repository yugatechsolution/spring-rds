package com.yuga.spring_rds.model.whatsapp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yuga.spring_rds.domain.whatsapp.util.BaseWhatsAppMessageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppMessageRequestModel extends BaseWhatsAppMessageRequest {

  @JsonProperty("messaging_product")
  private String messagingProduct;

  @JsonProperty("recipient_type")
  private String recipientType;

  private String to;
}
