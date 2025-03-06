package com.yuga.spring_rds.connector;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.yuga.spring_rds.model.whatsapp.request.WhatsAppMessageRequestModel;
import com.yuga.spring_rds.model.whatsapp.response.WhatsAppMessageResponseModel;
import com.yuga.spring_rds.model.whatsapp.response.WhatsAppTemplateResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class WhatsAppConnector {

  public static ObjectMapper mapper;

  static {
    mapper = new ObjectMapper();
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  @Autowired private WebClient webClient;

  public WhatsAppMessageResponseModel sendWhatsAppMessage(
      WhatsAppMessageRequestModel whatsAppMessageRequestModel) {
    log.info(
        "Calling WhatsApp message API to send message to phoneNumber={}",
        whatsAppMessageRequestModel.getTo());
    return webClient
        .post()
        .uri("/messages")
        .bodyValue(mapper.convertValue(whatsAppMessageRequestModel, JsonNode.class))
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
        .map(node -> mapper.convertValue(node, WhatsAppMessageResponseModel.class))
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
