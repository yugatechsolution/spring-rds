package com.yuga.spring_rds.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

  @Value("${whatsapp.api.url:https://graph.facebook.com/v21.0/}")
  private String whatsAppApiUrl;

  @Value("${whatsapp.api.token}")
  private String accessToken;

  @Value("${whatsapp.phone.number.id}")
  private String phoneNumberId;

  @Bean("graphApiWebClient")
  public WebClient graphApiWebClient(WebClient.Builder builder) {
    return builder
        .baseUrl(whatsAppApiUrl + phoneNumberId)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }
}
