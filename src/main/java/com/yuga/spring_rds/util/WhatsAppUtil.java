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
    var builder =
        WhatsAppMessageRequestModel.builder()
            .messagingProduct("whatsapp")
            .recipientType("individual")
            .to(phoneNumber);
    return switch (whatsAppMessageRequest.getType()) {
      case text ->
          builder.type(WhatsAppMessageType.text).text(whatsAppMessageRequest.getText()).build();
      case interactive ->
          builder
              .type(WhatsAppMessageType.interactive)
              .interactive(whatsAppMessageRequest.getInteractive())
              .build();
      default -> null;
    };
  }
}
