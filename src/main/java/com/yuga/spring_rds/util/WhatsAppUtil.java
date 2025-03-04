package com.yuga.spring_rds.util;

import com.yuga.spring_rds.model.request.BroadcastMessageRequest;
import com.yuga.spring_rds.model.whatsapp.request.WhatsAppMessageRequestModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WhatsAppUtil {

  public static WhatsAppMessageRequestModel buildWhatsAppTemplateMessageRequestModel(
      BroadcastMessageRequest broadcastMessageRequest, int index) {
    String phoneNumber = broadcastMessageRequest.getPhoneNumbers().get(index);
    switch (broadcastMessageRequest.getRequestType()) {
      case TEMPLATE ->
          WhatsAppMessageRequestModel.builder()
              .messagingProduct("whatsapp")
              .to(phoneNumber)
              .type("template")
              .template(
                  WhatsAppMessageRequestModel.Template.builder()
                      .name(broadcastMessageRequest.getTemplateMessageRequest().getTemplateName())
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
                      .body(broadcastMessageRequest.getTextMessageRequest().getTextBody())
                      .previewUrl(false)
                      .build())
              .build();
    }
    return null;
  }
}
