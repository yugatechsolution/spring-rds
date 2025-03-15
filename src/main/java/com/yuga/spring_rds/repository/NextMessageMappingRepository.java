package com.yuga.spring_rds.repository;

import com.yuga.spring_rds.domain.whatsapp.ChatbotMessage;
import com.yuga.spring_rds.domain.whatsapp.NextMessageMapping;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NextMessageMappingRepository extends JpaRepository<NextMessageMapping, Long> {

  List<NextMessageMapping> findByParentMessageId(Long parentMessageId);

  List<NextMessageMapping> findByParentMessage(ChatbotMessage parentMessage);

  @Transactional
  void deleteByParentMessage(ChatbotMessage parentMessage);
}
