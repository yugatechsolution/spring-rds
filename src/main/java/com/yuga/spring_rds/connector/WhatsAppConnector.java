package com.yuga.spring_rds.connector;

import com.fasterxml.jackson.databind.JsonNode;
import com.yuga.spring_rds.model.whatsapp.request.WhatsAppTemplateRequestModel;
import com.yuga.spring_rds.model.whatsapp.request.WhatsAppTextMessageRequestModel;
import com.yuga.spring_rds.model.whatsapp.response.WhatsAppMessageResponseModel;
import com.yuga.spring_rds.model.whatsapp.response.WhatsAppTemplateResponseModel;
import com.yuga.spring_rds.util.WhatsAppUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class WhatsAppConnector {

  @Autowired private WebClient webClient;

  public WhatsAppMessageResponseModel sendWhatsAppMessage(String phoneNumber, String messageBody) {
    WhatsAppTextMessageRequestModel whatsAppTextMessageRequestModel =
        WhatsAppUtil.buildWhatsAppTextMessageRequestModel(phoneNumber, messageBody);
    log.info("Calling WhatsApp message API to send message to phoneNumber={}", phoneNumber);
    return webClient
        .post()
        .uri("/messages")
        .bodyValue(whatsAppTextMessageRequestModel)
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            response ->
                response
                    .bodyToMono(JsonNode.class)
                    .map(body -> new RuntimeException("Client Error: " + body.toString())))
        .onStatus(
            HttpStatusCode::is5xxServerError,
            response ->
                response
                    .bodyToMono(JsonNode.class)
                    .map(body -> new RuntimeException("Server Error: " + body.toString())))
        .bodyToMono(WhatsAppMessageResponseModel.class)
        .doOnNext(res -> log.info("Success response from WhatsApp API: {}", res))
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
