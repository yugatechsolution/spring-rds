package com.yuga.spring_rds.domain.whatsapp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chatbot_triggers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotTrigger {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String triggerText;

  @ManyToOne
  @JoinColumn(name = "chatbot_message_id", nullable = false)
  private ChatbotMessage chatbotMessage;
}
