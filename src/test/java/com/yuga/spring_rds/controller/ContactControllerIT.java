package com.yuga.spring_rds.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuga.spring_rds.SpringRdsApplication;
import com.yuga.spring_rds.domain.Contact;
import com.yuga.spring_rds.domain.User;
import com.yuga.spring_rds.dto.ContactDTO;
import com.yuga.spring_rds.repository.ContactRepository;
import com.yuga.spring_rds.repository.UserRepository;
import com.yuga.spring_rds.util.JwtUtil;
import com.yuga.spring_rds.util.PasswordUtil;
import com.yuga.spring_rds.util.TestUtil;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
    jwtToken = "Bearer " + JwtUtil.generateToken(testUser.getUsername());
  }

  @Test
  void testBulkUploadContacts_Success() throws Exception {
    // Sample CSV data
    String csvData =
        "name,phoneNumber\n"
            + "John Doe,9876543210\n"
            + "Jane Smith,1234567890\n"
            + "Invalid User,12345\n";

    MockMultipartFile file =
        new MockMultipartFile(
            "file", "contacts.csv", "text/csv", csvData.getBytes(StandardCharsets.UTF_8));

    mockMvc
        .perform(
            multipart("/api/contacts/bulk-upload")
                .file(file)
                .header("Authorization", jwtToken)
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.savedContacts").isArray())
        .andExpect(jsonPath("$.savedContacts.length()").value(2)) // Only valid ones should be saved
        .andExpect(jsonPath("$.errors.length()").value(1)); // One invalid row
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
