package com.yuga.spring_rds.repository;

import com.yuga.spring_rds.domain.ChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
  List<ChatMessage> findByWaIdOrderByTimestampAsc(String waId);

  List<ChatMessage> findByWaIdAndPhoneNumberIdOrderByTimestampAsc(
      String waId, String phoneNumberId);
}
