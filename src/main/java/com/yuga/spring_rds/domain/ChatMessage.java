package com.yuga.spring_rds.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_messages")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String phoneNumberId;

  @Column(nullable = false)
  private String waId;

  @Column(unique = true, nullable = false)
  private String messageId;

  @Column(nullable = false)
  private Long timestamp;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MessageType messageType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Direction direction;

  @Column(columnDefinition = "TEXT") // For text messages or general content
  private String messageBody;

  @Column(columnDefinition = "JSON") // Store complex types (like buttons, lists, etc.)
  private String metadata;

  public enum MessageType {
    TEXT,
    BUTTON,
    LIST,
    FLOW,
    IMAGE,
    DOCUMENT,
    AUDIO,
    VIDEO
  }

  public enum Direction {
    INCOMING,
    OUTGOING
  }
}
