package com.yuga.spring_rds.repository;

import com.yuga.spring_rds.domain.whatsapp.ChatbotMessage;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatbotMessageRepository extends JpaRepository<ChatbotMessage, Long> {

  @EntityGraph(value = "ChatbotMessage.fullFlow", type = EntityGraph.EntityGraphType.LOAD)
  Optional<ChatbotMessage> findById(Long id);
}
