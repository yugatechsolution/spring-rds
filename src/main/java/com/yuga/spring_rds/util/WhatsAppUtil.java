package com.yuga.spring_rds.util;

import com.yuga.spring_rds.domain.whatsapp.WhatsAppMessageType;
import com.yuga.spring_rds.model.api.request.WhatsAppMessageRequest;
import com.yuga.spring_rds.model.whatsapp.request.WhatsAppMessageRequestModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WhatsAppUtil {

  public static WhatsAppMessageRequestModel buildWhatsAppTemplateMessageRequestModel(
      WhatsAppMessageRequest whatsAppMessageRequest, int index) {
    String phoneNumber = whatsAppMessageRequest.getPhoneNumbers().get(index);
    return switch (whatsAppMessageRequest.getType()) {
      case text ->
          WhatsAppMessageRequestModel.builder()
              .messagingProduct("whatsapp")
              .recipientType("individual")
              .to(phoneNumber)
              .type(WhatsAppMessageType.text)
              .text(whatsAppMessageRequest.getText())
              .build();
      default -> null;
    };
  }
}
