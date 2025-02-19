package com.yuga.spring_rds.service;

import com.yuga.spring_rds.dto.ContactDTO;
import com.yuga.spring_rds.model.Contact;
import com.yuga.spring_rds.model.ContactId;
import com.yuga.spring_rds.model.User;
import com.yuga.spring_rds.repository.ContactRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class ContactService {

  @Autowired private ContactRepository contactRepository;

  /** Bulk add contacts to the database. */
  public List<Contact> bulkAddContacts(User user, List<ContactDTO> contactDTOs) {
    List<Contact> contacts =
        contactDTOs.stream()
            .map(
                dto ->
                    new Contact(
                        new ContactId(user.getId(), dto.getPhoneNumber()), dto.getName(), user))
            .toList();

    return contactRepository.saveAll(contacts);
  }

  public List<ContactDTO> getContactsByUserId(Long userId) {
    return contactRepository.findByIdUserId(userId).stream()
        .map(
            contact ->
                ContactDTO.builder()
                    .name(contact.getName())
                    .phoneNumber(contact.getId().getPhoneNumber())
                    .build())
        .collect(Collectors.toList());
  }

  public ContactDTO addContact(User user, ContactDTO contactDTO) {
    Contact contact =
        contactRepository.save(
            Contact.builder()
                .id(
                    ContactId.builder()
                        .phoneNumber(contactDTO.getPhoneNumber())
                        .userId(user.getId())
                        .build())
                .name(contactDTO.getName())
                .user(user)
                .build());
    return contactDTO;
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

  /** Processes the bulk CSV file upload using Apache Commons CSV. */
  public Map<String, Object> processBulkUpload(MultipartFile file, User user) {
    List<ContactDTO> validContacts = new ArrayList<>();
    List<String> errors = new ArrayList<>();
    if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
      return Map.of("message", "Invalid file. Please upload a non-empty CSV file.");
    }
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        CSVParser csvParser =
            CSVFormat.DEFAULT
                .builder()
                .setHeader() // Auto-detects header
                .setSkipHeaderRecord(true) // Skips the first row
                .setIgnoreHeaderCase(true)
                .setTrim(true) // Removes unnecessary spaces
                .build()
                .parse(reader)) {
      for (CSVRecord record : csvParser) {
        String name = record.get("name");
        String phoneNumber = record.get("phoneNumber");
        if (name == null || name.isEmpty() || phoneNumber == null || phoneNumber.isEmpty()) {
          errors.add("Missing name or phone number in row: " + record);
          continue;
        }
        if (!phoneNumber.matches("\\d{10}")) {
          errors.add("Invalid phone number format in row: " + record);
          continue;
        }
        validContacts.add(new ContactDTO(name, phoneNumber));
      }
      List<Contact> savedContacts = bulkAddContacts(user, validContacts);
      return Map.of("savedContacts", savedContacts, "errors", errors);
    } catch (IOException e) {
      log.error("Error processing CSV file: {}", e.getMessage());
      return Map.of("message", "Error processing CSV file");
    }
  }
}
