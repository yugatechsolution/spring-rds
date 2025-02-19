package com.yuga.spring_rds.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.SpringRdsApplication;
import com.yuga.spring_rds.dto.ContactDTO;
import com.yuga.spring_rds.model.Contact;
import com.yuga.spring_rds.model.User;
import com.yuga.spring_rds.repository.ContactRepository;
import com.yuga.spring_rds.repository.UserRepository;
import com.yuga.spring_rds.util.JwtUtil;
import com.yuga.spring_rds.util.PasswordUtil;
import com.yuga.spring_rds.util.TestUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = SpringRdsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test") // Uses application-test.properties
@Rollback // Rolls back all DB changes after each test
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Enables @Order
class ContactControllerIT {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ContactRepository contactRepository;
  @Autowired private UserRepository userRepository;

  private String jwtToken;
  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = TestUtil.getUser();
    testUser.setPassword(PasswordUtil.hashPassword(testUser.getPassword()));
    testUser =
        userRepository
            .findByUsernameOrEmail(testUser.getUsername(), testUser.getEmail())
            .orElseGet(() -> userRepository.saveAndFlush(testUser));
    contactRepository.findByIdUserId(testUser.getId()).forEach(contactRepository::delete);
    jwtToken = "Bearer " + JwtUtil.generateToken(testUser.getId());
  }

  @Test
  void testGetContacts_EmptyList() throws Exception {
    mockMvc
        .perform(get("/api/contacts").header("Authorization", jwtToken))
        .andExpect(status().isOk())
        .andExpect(content().json("[]")); // Expect empty list
  }

  @Test
  void testAddContact_Success() throws Exception {
    ContactDTO newContact = TestUtil.getContactDTO();
    String contactJson = objectMapper.writeValueAsString(newContact);

    mockMvc
        .perform(
            post("/api/contacts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(contactJson))
        .andExpect(status().isCreated());

    // Verify in DB
    List<Contact> contacts = contactRepository.findByIdUserId(testUser.getId());
    assertThat(contacts).hasSize(1);
    assertThat(contacts.get(0).getName()).isEqualTo(newContact.getName());
  }

  @Test
  void testAddContact_Unauthorized() throws Exception {
    Contact newContact = TestUtil.getContact();
    String contactJson = objectMapper.writeValueAsString(newContact);

    mockMvc
        .perform(post("/api/contacts").contentType(MediaType.APPLICATION_JSON).content(contactJson))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testDeleteContact_Success() throws Exception {
    Contact contact = TestUtil.getContact(testUser);
    contactRepository.saveAndFlush(contact);

    mockMvc
        .perform(
            delete("/api/contacts")
                .queryParam("phoneNumber", contact.getId().getPhoneNumber())
                .header("Authorization", jwtToken))
        .andExpect(status().isNoContent());

    // Verify it's deleted
    assertThat(contactRepository.findByIdUserId(testUser.getId())).isEmpty();
  }

  @Test
  void testDeleteContact_NotFound() throws Exception {
    mockMvc
        .perform(delete("/api/contacts?phoneNumber=3333222111").header("Authorization", jwtToken))
        .andExpect(status().isNotFound());
  }
}
