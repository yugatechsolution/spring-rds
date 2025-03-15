package com.yuga.spring_rds.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.yuga.spring_rds.domain.whatsapp.ChatbotMessage;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_message_mapping")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChatMessageMapping {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "chat_message_id", referencedColumnName = "messageId")
  private ChatMessage chatMessage;

  @ManyToOne
  @JoinColumn(name = "chatbot_message_id", referencedColumnName = "id")
  private ChatbotMessage chatbotMessage;

  @CreationTimestamp private LocalDateTime createdAt;
}
