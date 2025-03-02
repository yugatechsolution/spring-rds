package com.yuga.spring_rds.util;

import com.yuga.spring_rds.model.whatsapp.request.WhatsAppMessageRequestModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WhatsAppUtil {

  public static WhatsAppMessageRequestModel buildWhatsAppTemplateMessageRequestModel(
      String phoneNumber, String templateName) {
    return WhatsAppMessageRequestModel.builder()
        .messagingProduct("whatsapp")
        .to(phoneNumber)
        .type("template")
        .template(
            WhatsAppMessageRequestModel.Template.builder()
                .name(templateName)
                .language(new WhatsAppMessageRequestModel.Language("en_US"))
                .build())
        .build();
  }
}
