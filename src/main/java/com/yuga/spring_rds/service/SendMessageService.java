package com.yuga.spring_rds.service;

import com.yuga.spring_rds.connector.WhatsAppConnector;
import com.yuga.spring_rds.model.api.request.WhatsAppMessageRequest;
import com.yuga.spring_rds.model.api.response.SendMessageResponse;
import com.yuga.spring_rds.model.whatsapp.request.WhatsAppMessageRequestModel;
import com.yuga.spring_rds.model.whatsapp.response.WhatsAppTemplateResponseModel;
import com.yuga.spring_rds.util.WhatsAppUtil;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SendMessageService {

  @Autowired private WhatsAppConnector whatsAppConnector;
  @Autowired private ChatService chatService;

  public SendMessageResponse sendMessage(WhatsAppMessageRequest request) {
    return SendMessageResponse.builder()
        .responseDetails(
            IntStream.range(0, request.getPhoneNumbers().size())
                .mapToObj(
                    index -> WhatsAppUtil.buildWhatsAppTemplateMessageRequestModel(request, index))
                .filter(Objects::nonNull)
                .collect(
                    Collectors.toMap(
                        WhatsAppMessageRequestModel::getTo,
                        waReqModel -> {
                          try {
                            var response = whatsAppConnector.sendWhatsAppMessage(waReqModel);
                            chatService.saveOutgoingMessage(waReqModel, response);
                            return response;
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
