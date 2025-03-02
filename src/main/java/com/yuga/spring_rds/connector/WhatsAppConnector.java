package com.yuga.spring_rds.connector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.model.whatsapp.request.WhatsAppMessageRequestModel;
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

  public WhatsAppMessageResponseModel sendWhatsAppMessage(String phoneNumber, String templateName) {
    WhatsAppMessageRequestModel whatsAppMessageRequestModel =
        WhatsAppUtil.buildWhatsAppTemplateMessageRequestModel(phoneNumber, templateName);
    log.info("Calling WhatsApp message API to send message to phoneNumber={}", phoneNumber);
    return webClient
        .post()
        .uri("/messages")
        .bodyValue(whatsAppMessageRequestModel)
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
        .bodyToMono(JsonNode.class)
        .doOnNext(res -> log.info("Success response from WhatsApp API: {}", res))
        .map(node -> new ObjectMapper().convertValue(node, WhatsAppMessageResponseModel.class))
        .block();
  }

  public WhatsAppTemplateResponseModel createTemplate(
      String phoneNumberId, WhatsAppMessageRequestModel request) {
    return webClient
        .post()
        .uri("/message_templates")
        .bodyValue(request)
        .retrieve()
        .bodyToMono(WhatsAppTemplateResponseModel.class)
        .block();
  }
}
