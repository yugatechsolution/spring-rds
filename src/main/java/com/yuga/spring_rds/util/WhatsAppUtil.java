package com.yuga.spring_rds.util;

import com.yuga.spring_rds.model.whatsapp.WhatsAppMessageRequestModel;
import com.yuga.spring_rds.model.whatsapp.WhatsAppTemplateRequestModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WhatsAppUtil {

  public static WhatsAppMessageRequestModel buildWhatsAppMessageRequestModel(
      String phoneNumber, String message) {
    return WhatsAppMessageRequestModel.builder()
        .messagingProduct("whatsapp")
        .to(phoneNumber)
        .type("text")
        .recipientType("individual")
        .text(WhatsAppMessageRequestModel.MessageText.builder().body(message).build())
        .build();
  }

  public static WhatsAppTemplateRequestModel buildWhatsAppTemplateRequestModel() {
    return WhatsAppTemplateRequestModel.builder().build();
  }
}
