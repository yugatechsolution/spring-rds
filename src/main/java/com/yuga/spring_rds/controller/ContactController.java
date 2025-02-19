package com.yuga.spring_rds.controller;

import com.yuga.spring_rds.advice.RequestContext;
import com.yuga.spring_rds.dto.ContactDTO;
import com.yuga.spring_rds.service.ContactService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

  @Autowired private ContactService contactService;

  @GetMapping
  public ResponseEntity<List<ContactDTO>> getAllContacts() {
    List<ContactDTO> contacts = contactService.getContactsByUserId(RequestContext.getUserId());
    return ResponseEntity.ok(contacts);
  }

  /** Bulk upload contacts from CSV file */
  @PostMapping(value = "/bulk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Map<String, Object>> bulkUploadContacts(
      @RequestParam("file") MultipartFile file) {
    return ResponseEntity.ok(contactService.processBulkUpload(file, RequestContext.getUser()));
  }

  @PostMapping
  public ResponseEntity<ContactDTO> addContact(@RequestBody ContactDTO contactDTO) {
    ContactDTO savedContact = contactService.addContact(RequestContext.getUser(), contactDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedContact);
  }

  @DeleteMapping
  public ResponseEntity<String> deleteContact(@RequestParam String phoneNumber) {
    try {
      contactService.deleteContact(RequestContext.getUserId(), phoneNumber);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted successfully!");
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }
}
