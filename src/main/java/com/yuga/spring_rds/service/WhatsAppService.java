package com.yuga.spring_rds.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.yuga.spring_rds.connector.WhatsAppConnector;
import com.yuga.spring_rds.model.request.BroadcastMessageTemplateRequest;
import com.yuga.spring_rds.model.response.BroadcastMessageTemplateResponse;
import com.yuga.spring_rds.model.whatsapp.request.WhatsAppMessageRequestModel;
import com.yuga.spring_rds.model.whatsapp.response.WhatsAppTemplateResponseModel;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhatsAppService {

  @Autowired private WhatsAppConnector whatsAppConnector;

  public BroadcastMessageTemplateResponse broadcastWhatsAppMessageTemplate(
      BroadcastMessageTemplateRequest request) {
    return BroadcastMessageTemplateResponse.builder()
        .responseDetails(
            request.getPhoneNumbers().stream()
                .collect(
                    Collectors.toMap(
                        phoneNo -> phoneNo,
                        phoneNo -> {
                          try {
                            JsonNode response =
                                whatsAppConnector.sendWhatsAppMessage(
                                    phoneNo, request.getTemplateName());
                            return "Message is sent successfully!";
                          } catch (Exception e) {
                            return e.getMessage();
                          }
                        })))
        .build();
  }

  public WhatsAppTemplateResponseModel createTemplate(
      String phoneNumberId, WhatsAppMessageRequestModel request) {
    return whatsAppConnector.createTemplate(phoneNumberId, request);
  }
}
