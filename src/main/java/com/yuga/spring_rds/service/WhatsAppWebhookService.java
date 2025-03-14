package com.yuga.spring_rds.service;

import com.yuga.spring_rds.model.api.request.WhatsAppWebhookRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhatsAppWebhookService {

  @Autowired private WhatsAppContactService whatsAppContactService;
  @Autowired private ChatService chatService;

  public void processIncomingMessage(WhatsAppWebhookRequest request) {
    request
        .getEntry()
        .forEach(
            entry ->
                entry
                    .getChanges()
                    .forEach(
                        change -> {
                          WhatsAppWebhookRequest.Value value = change.getValue();
                          String phoneNumberId = value.getMetadata().getPhoneNumberId();

                          // Save contact (Ensure uniqueness per phoneNumberId)
                          value
                              .getContacts()
                              .forEach(
                                  contact -> {
                                    whatsAppContactService.saveContact(phoneNumberId, contact);
                                  });

                          // Save messages
                          value
                              .getMessages()
                              .forEach(
                                  message ->
                                      chatService.saveIncomingMessage(phoneNumberId, message));
                        }));
  }
}
