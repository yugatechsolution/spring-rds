package com.yuga.spring_rds.repository;

import com.yuga.spring_rds.domain.whatsapp.ChatbotMessage;
import com.yuga.spring_rds.domain.whatsapp.ChatbotTrigger;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatbotTriggerRepository extends JpaRepository<ChatbotTrigger, Long> {

  // Fetch only the trigger (startingMessage will be lazy-loaded when accessed)
  Optional<ChatbotTrigger> findByTriggerText(String triggerText);

  // Fetch the entire flow (trigger, starting message, and next messages)
  @EntityGraph(value = "ChatbotMessage.fullFlow", type = EntityGraph.EntityGraphType.LOAD)
  Optional<ChatbotTrigger> findFullFlowByTriggerText(String triggerText);

  // Fetch only the starting message (if needed separately)
  @Query("SELECT t.startingMessage FROM ChatbotTrigger t WHERE t.triggerText = :triggerText")
  Optional<ChatbotMessage> findStartingMessage(@Param("triggerText") String triggerText);
}
