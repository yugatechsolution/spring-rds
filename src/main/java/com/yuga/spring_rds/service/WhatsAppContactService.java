package com.yuga.spring_rds.service;

import com.yuga.spring_rds.domain.WhatsAppContact;
import com.yuga.spring_rds.domain.WhatsAppContactId;
import com.yuga.spring_rds.dto.ContactDTO;
import com.yuga.spring_rds.model.api.request.WhatsAppWebhookRequest;
import com.yuga.spring_rds.repository.WhatsAppContactRepository;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhatsAppContactService {

  @Value("${whatsapp.phone.number.id}")
  private String phoneNumberId;

  @Autowired private WhatsAppContactRepository whatsAppContactRepository;

  public List<ContactDTO> getAllWhatsAppContacts() {
    return whatsAppContactRepository.findByPhoneNumberId(phoneNumberId).stream()
        .map(
            whatsAppContact ->
                ContactDTO.builder()
                    .name(whatsAppContact.getDisplayName())
                    .phoneNumber(whatsAppContact.getWaId())
                    .build())
        .collect(Collectors.toList());
  }

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
    log.info("Saving contact: {}", contact);
    return whatsAppContactRepository
        .findById(new WhatsAppContactId(contact.getPhoneNumberId(), contact.getWaId()))
        .orElseGet(() -> whatsAppContactRepository.save(contact));
  }

  public void deleteWhatsAppContact(WhatsAppContactId whatsAppContactId) {
    log.info("Deleting Whatsapp contact: {}", whatsAppContactId);
    whatsAppContactRepository
        .findById(whatsAppContactId)
        .ifPresent(whatsAppContactRepository::delete);
  }
}
