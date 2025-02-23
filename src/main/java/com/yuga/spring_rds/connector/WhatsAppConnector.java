package com.yuga.spring_rds.connector;

import com.yuga.spring_rds.model.whatsapp.WhatsAppMessageRequestModel;
import com.yuga.spring_rds.model.whatsapp.WhatsAppMessageResponseModel;
import com.yuga.spring_rds.model.whatsapp.WhatsAppTemplateRequestModel;
import com.yuga.spring_rds.model.whatsapp.WhatsAppTemplateResponseModel;
import com.yuga.spring_rds.util.WhatsAppUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class WhatsAppConnector {

  @Autowired private WebClient webClient;

  public WhatsAppMessageResponseModel sendWhatsAppMessage(String phoneNumber, String message) {
    WhatsAppMessageRequestModel whatsAppMessageRequestModel =
        WhatsAppUtil.buildWhatsAppMessageRequestModel(phoneNumber, message);
    log.info("Calling WhatsApp message API to send message to phoneNumber={}", phoneNumber);
    return webClient
        .post()
        .uri("/messages")
        .bodyValue(whatsAppMessageRequestModel)
        .retrieve()
        .bodyToMono(WhatsAppMessageResponseModel.class)
        .block();
  }

  public WhatsAppTemplateResponseModel createTemplate(
      String phoneNumberId, WhatsAppTemplateRequestModel request) {
    return webClient
        .post()
        .uri("/message_templates")
        .bodyValue(request)
        .retrieve()
        .bodyToMono(WhatsAppTemplateResponseModel.class)
        .block();
  }
}
