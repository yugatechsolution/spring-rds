package com.yuga.spring_rds.service;

import com.yuga.spring_rds.model.request.BroadcastMessageRequest;
import com.yuga.spring_rds.model.whatsapp.WhatsAppRequestModel;
import com.yuga.spring_rds.model.whatsapp.WhatsAppResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class WhatsAppService {

  @Autowired private WebClient webClient;

  @Value("${whatsapp.api.token}")
  private String accessToken;

  @Value("${whatsapp.phone.number.id}")
  private String phoneNumberId;

  public WhatsAppResponseModel sendWhatsAppMessage(BroadcastMessageRequest request) {

    String phoneNumber = request.getPhoneNumbers().getFirst();
    WhatsAppRequestModel whatsAppRequestModel =
        WhatsAppRequestModel.builder()
            .messagingProduct("whatsapp")
            .to(phoneNumber)
            .type("text")
            .recipientType("individual")
            .text(WhatsAppRequestModel.MessageText.builder().body(request.getMessage()).build())
            .build();

    log.info("Calling WhatsApp message API to send message to phoneNumber={}", phoneNumber);
    return webClient
        .post()
        .uri(String.format("/%s/messages", phoneNumberId))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .bodyValue(whatsAppRequestModel)
        .retrieve()
        .bodyToMono(WhatsAppResponseModel.class)
        .block(); // Blocking for simplicity, consider Mono if reactive
  }
}
