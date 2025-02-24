package com.yuga.spring_rds.util;

import com.yuga.spring_rds.model.whatsapp.request.WhatsAppTemplateRequestModel;
import com.yuga.spring_rds.model.whatsapp.request.WhatsAppTextMessageRequestModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WhatsAppUtil {

  public static WhatsAppTextMessageRequestModel buildWhatsAppTextMessageRequestModel(
      String phoneNumber, String body) {
    return WhatsAppTextMessageRequestModel.builder()
        .messagingProduct("whatsapp")
        .to(phoneNumber)
        .type("text")
        .text(WhatsAppTextMessageRequestModel.Text.builder().body(body).build())
        .build();
  }

  public static WhatsAppTemplateRequestModel buildWhatsAppTemplateRequestModel() {
    return WhatsAppTemplateRequestModel.builder().build();
  }
}
