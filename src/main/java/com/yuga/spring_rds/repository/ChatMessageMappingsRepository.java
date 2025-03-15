package com.yuga.spring_rds.repository;

import com.yuga.spring_rds.domain.ChatMessageMapping;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageMappingsRepository extends JpaRepository<ChatMessageMapping, Long> {
  @Query("SELECT cmm FROM ChatMessageMapping cmm WHERE cmm.chatMessage.messageId = :messageId")
  Optional<ChatMessageMapping> findByMessageId(@Param("messageId") String messageId);
}
