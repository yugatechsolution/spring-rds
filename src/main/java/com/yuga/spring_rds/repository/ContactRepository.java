package com.yuga.spring_rds.repository;

import com.yuga.spring_rds.domain.Contact;
import com.yuga.spring_rds.domain.ContactId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, ContactId> {
  List<Contact> findByIdUserId(Long userId); // Fetch contacts by userId
}
