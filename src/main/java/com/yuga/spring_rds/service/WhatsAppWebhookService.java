package com.yuga.spring_rds.service;

import com.yuga.spring_rds.model.api.request.WhatsAppWebhookRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class WhatsAppWebhookService {
  @Autowired private WhatsAppContactService whatsAppContactService;
  @Autowired private ChatService chatService;
  @Autowired private WhatsAppWebhookMessageHandler whatsAppWebhookMessageHandler;

  public void processIncomingMessage(WhatsAppWebhookRequest request) {
    if (request == null || request.getEntry() == null) {
      log.warn("Received null or empty webhook request");
      return;
    }

    request
        .getEntry()
        .forEach(
            entry ->
                entry
                    .getChanges()
                    .forEach(
                        change -> {
                          var value = change.getValue();
                          if (value == null || value.getMetadata() == null) {
                            log.warn("Invalid value or metadata in change");
                            return;
                          }

                          String phoneNumberId = value.getMetadata().getPhoneNumberId();
                          if (phoneNumberId == null) {
                            log.warn("Phone number ID is null in metadata");
                            return;
                          }

                          // Save contacts
                          if (!CollectionUtils.isEmpty(value.getContacts()))
                            value
                                .getContacts()
                                .forEach(contact -> saveContact(phoneNumberId, contact));

                          // Save messages
                          if (!CollectionUtils.isEmpty(value.getMessages()))
                            value
                                .getMessages()
                                .forEach(
                                    message ->
                                        whatsAppWebhookMessageHandler.handleMessage(
                                            phoneNumberId, message));

                          // Handle statuses
                          if (!CollectionUtils.isEmpty(value.getStatuses()))
                            value
                                .getStatuses()
                                .forEach(status -> handleStatus(phoneNumberId, status));
                        }));
  }

  private void saveContact(String phoneNumberId, WhatsAppWebhookRequest.Contact contact) {
    try {
      whatsAppContactService.saveContact(phoneNumberId, contact);
    } catch (Exception e) {
      log.error("Failed to save contact for phoneNumberId={}", phoneNumberId, e);
    }
  }

  private void handleStatus(String phoneNumberId, WhatsAppWebhookRequest.Status status) {
    try {
      log.info("Received status for phoneNumberId={}, status={}", phoneNumberId, status);
      chatService.updateMessageStatus(status.getId(), status.getStatus().toUpperCase());
    } catch (Exception e) {
      log.error("Failed to handle status for phoneNumberId={}", phoneNumberId, e);
    }
  }
}
