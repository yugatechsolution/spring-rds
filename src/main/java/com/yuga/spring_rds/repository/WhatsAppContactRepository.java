package com.yuga.spring_rds.repository;

import com.yuga.spring_rds.domain.WhatsAppContact;
import com.yuga.spring_rds.domain.WhatsAppContactId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WhatsAppContactRepository
    extends JpaRepository<WhatsAppContact, WhatsAppContactId> {
  List<WhatsAppContact> findByPhoneNumberId(String phoneNumberId);
}
