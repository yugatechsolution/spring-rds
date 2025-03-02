package com.yuga.spring_rds.service;

import com.yuga.spring_rds.connector.WhatsAppConnector;
import com.yuga.spring_rds.model.request.BroadcastMessageTemplateRequest;
import com.yuga.spring_rds.model.response.BroadcastMessageTemplateResponse;
import com.yuga.spring_rds.model.whatsapp.request.WhatsAppMessageRequestModel;
import com.yuga.spring_rds.model.whatsapp.response.WhatsAppTemplateResponseModel;
import com.yuga.spring_rds.util.Constants;
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
        .statusMap(
            request.getPhoneNumbers().stream()
                .collect(
                    Collectors.toMap(
                        phoneNo -> phoneNo,
                        phoneNo -> {
                          try {
                            whatsAppConnector.sendWhatsAppMessage(
                                phoneNo, request.getTemplateName());
                            return Constants.Status.SUCCESS;
                          } catch (Exception e) {
                            return Constants.Status.FAILURE;
                          }
                        })))
        .build();
  }

  public WhatsAppTemplateResponseModel createTemplate(
      String phoneNumberId, WhatsAppMessageRequestModel request) {
    return whatsAppConnector.createTemplate(phoneNumberId, request);
  }
}
