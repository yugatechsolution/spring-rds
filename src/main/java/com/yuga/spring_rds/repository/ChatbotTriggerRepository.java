package com.yuga.spring_rds.repository;

import com.yuga.spring_rds.domain.whatsapp.ChatbotTrigger;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatbotTriggerRepository extends JpaRepository<ChatbotTrigger, Long> {

  Optional<ChatbotTrigger> findByTriggerText(String triggerText);
}
