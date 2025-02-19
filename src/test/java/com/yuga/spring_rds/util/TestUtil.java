package com.yuga.spring_rds.util;

import com.yuga.spring_rds.dto.ContactDTO;
import com.yuga.spring_rds.model.Contact;
import com.yuga.spring_rds.model.ContactId;
import com.yuga.spring_rds.model.User;

public class TestUtil {

  public static final String USERNAME = "testUser";
  public static final String EMAIL = "testUser@example.com";
  public static final String PASSWORD = "password123";
  public static final String PHONE_NUMBER = "1234567890";

  public static User getUser() {
    return User.builder().username(USERNAME).email(EMAIL).password(PASSWORD).build();
  }

  public static Contact getContact() {
    User testUser = getUser();
    return new Contact(new ContactId(testUser.getId(), PHONE_NUMBER), "John Doe", testUser);
  }

  public static Contact getContact(User user) {
    return new Contact(new ContactId(user.getId(), PHONE_NUMBER), "John Doe", user);
  }

  public static ContactDTO getContactDTO() {
    User testUser = getUser();
    return ContactDTO.builder().phoneNumber(PHONE_NUMBER).name("John Doe").build();
  }
}
