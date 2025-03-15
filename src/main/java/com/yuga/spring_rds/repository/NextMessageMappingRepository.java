package com.yuga.spring_rds.repository;

import com.yuga.spring_rds.domain.whatsapp.ChatbotMessage;
import com.yuga.spring_rds.domain.whatsapp.NextMessageMapping;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NextMessageMappingRepository extends JpaRepository<NextMessageMapping, Long> {

  List<NextMessageMapping> findByParentMessageId(Long parentMessageId);

  List<NextMessageMapping> findByParentMessage(ChatbotMessage parentMessage);

  @Transactional
  void deleteByParentMessage(ChatbotMessage parentMessage);

  @Query(
      "SELECT nmm FROM NextMessageMapping nmm WHERE nmm.parentMessage = :parentMessage AND nmm.actionTrigger = :actionTrigger")
  Optional<NextMessageMapping> findByParentMessageAndActionTrigger(
      @Param("parentMessage") ChatbotMessage parentMessage,
      @Param("actionTrigger") String actionTrigger);
}
