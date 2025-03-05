package com.yuga.spring_rds.service;

import com.yuga.spring_rds.domain.WhatsAppContact;
import com.yuga.spring_rds.domain.WhatsAppContactId;
import com.yuga.spring_rds.model.api.request.WhatsAppWebhookRequest;
import com.yuga.spring_rds.repository.WhatsAppContactRepository;
import jakarta.transaction.Transactional;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppContactService {

  @Autowired private WhatsAppContactRepository whatsAppContactRepository;

  public WhatsAppContact saveContact(String phoneNumberId, WhatsAppWebhookRequest.Contact contact) {
    WhatsAppContact whatsAppContact =
        WhatsAppContact.builder()
            .phoneNumberId(phoneNumberId)
            .waId(contact.getWaId())
            .displayName(contact.getProfile().getName())
            .registeredAt(new Date())
            .build();
    return this.saveIfNotExists(whatsAppContact);
  }

  @Transactional
  public WhatsAppContact saveIfNotExists(WhatsAppContact contact) {
    return whatsAppContactRepository
        .findById(new WhatsAppContactId(contact.getPhoneNumberId(), contact.getWaId()))
        .orElseGet(() -> whatsAppContactRepository.save(contact));
  }
}
