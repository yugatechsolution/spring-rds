package com.yuga.spring_rds.util;

import com.yuga.spring_rds.model.api.request.SendMessageRequest;
import com.yuga.spring_rds.model.whatsapp.request.WhatsAppMessageRequestModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WhatsAppUtil {

  public static WhatsAppMessageRequestModel buildWhatsAppTemplateMessageRequestModel(
      SendMessageRequest sendMessageRequest, int index) {
    String phoneNumber = sendMessageRequest.getPhoneNumbers().get(index);
    return switch (sendMessageRequest.getRequestType()) {
      case TEMPLATE ->
          WhatsAppMessageRequestModel.builder()
              .messagingProduct("whatsapp")
              .to(phoneNumber)
              .type("template")
              .template(
                  WhatsAppMessageRequestModel.Template.builder()
                      .name(sendMessageRequest.getTemplateMessageRequest().getTemplateName())
                      .language(new WhatsAppMessageRequestModel.Language("en_US"))
                      .build())
              .build();

      case TEXT ->
          WhatsAppMessageRequestModel.builder()
              .messagingProduct("whatsapp")
              .recipientType("individual")
              .to(phoneNumber)
              .type("text")
              .text(
                  WhatsAppMessageRequestModel.Text.builder()
                      .body(sendMessageRequest.getTextMessageRequest().getTextBody())
                      .previewUrl(false)
                      .build())
              .build();
    };
  }
}
