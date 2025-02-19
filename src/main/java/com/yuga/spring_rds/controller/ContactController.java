package com.yuga.spring_rds.controller;

import com.yuga.spring_rds.advice.RequestContext;
import com.yuga.spring_rds.dto.ContactDTO;
import com.yuga.spring_rds.model.Contact;
import com.yuga.spring_rds.service.ContactService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

  @Autowired private ContactService contactService;

  @GetMapping
  public ResponseEntity<List<Contact>> getAllContacts() {
    List<Contact> contacts = contactService.getContactsByUserId(RequestContext.getUserId());
    return ResponseEntity.ok(contacts);
  }

  @PostMapping
  public ResponseEntity<Contact> addContact(@RequestBody ContactDTO contactDTO) {
    Contact savedContact = contactService.addContact(RequestContext.getUser(), contactDTO);
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
