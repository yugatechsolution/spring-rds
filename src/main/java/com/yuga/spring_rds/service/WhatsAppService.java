package com.yuga.spring_rds.service;

import com.yuga.spring_rds.connector.WhatsAppConnector;
import com.yuga.spring_rds.model.request.BroadcastMessageRequest;
import com.yuga.spring_rds.model.whatsapp.WhatsAppMessageResponseModel;
import com.yuga.spring_rds.model.whatsapp.WhatsAppTemplateRequestModel;
import com.yuga.spring_rds.model.whatsapp.WhatsAppTemplateResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhatsAppService {

  @Autowired private WhatsAppConnector whatsAppConnector;

  public WhatsAppMessageResponseModel sendWhatsAppMessage(BroadcastMessageRequest request) {
    return whatsAppConnector.sendWhatsAppMessage(
        request.getPhoneNumbers().getFirst(), request.getMessage());
  }

  public WhatsAppTemplateResponseModel createTemplate(
      String phoneNumberId, WhatsAppTemplateRequestModel request) {
    return whatsAppConnector.createTemplate(phoneNumberId, request);
  }
}
