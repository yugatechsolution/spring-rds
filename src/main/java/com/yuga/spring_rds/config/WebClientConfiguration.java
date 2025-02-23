package com.yuga.spring_rds.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

  @Bean("graphApiWebClient")
  public WebClient graphApiWebClient(WebClient.Builder builder) {
    return builder.baseUrl("https://graph.facebook.com/v18.0").build();
  }
}
