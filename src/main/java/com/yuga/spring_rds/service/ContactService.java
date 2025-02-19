package com.yuga.spring_rds.service;

import com.yuga.spring_rds.dto.ContactDTO;
import com.yuga.spring_rds.model.Contact;
import com.yuga.spring_rds.model.ContactId;
import com.yuga.spring_rds.model.User;
import com.yuga.spring_rds.repository.ContactRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

  @Autowired private ContactRepository contactRepository;

  public List<Contact> getContactsByUserId(Long userId) {
    return contactRepository.findByIdUserId(userId);
  }

  public Contact addContact(User user, ContactDTO contactDTO) {
    return contactRepository.save(
        Contact.builder()
            .id(
                ContactId.builder()
                    .phoneNumber(contactDTO.getPhoneNumber())
                    .userId(user.getId())
                    .build())
            .name(contactDTO.getName())
            .user(user)
            .build());
  }

  public void deleteContact(Long userId, String phoneNumber) {
    ContactId contactId = ContactId.builder().userId(userId).phoneNumber(phoneNumber).build();
    contactRepository
        .findById(contactId)
        .ifPresentOrElse(
            contactRepository::delete,
            () -> {
              throw new RuntimeException("Contact not found with phoneNumber: " + phoneNumber);
            });
  }
}
