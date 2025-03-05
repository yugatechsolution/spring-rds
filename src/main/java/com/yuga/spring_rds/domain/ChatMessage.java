package com.yuga.spring_rds.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_messages")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // Ensure snake_case in JSON
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String phoneNumberId;
  private String waId;
  private String messageId;
  private Long timestamp;
  private String messageBody; // For text messages

  @Enumerated(EnumType.STRING)
  private MessageType messageType;

  @Enumerated(EnumType.STRING)
  private Direction direction;

  @Column(columnDefinition = "JSON") // Store additional JSON data
  private String metadata;

  public enum Direction {
    INCOMING,
    OUTGOING
  }

  public enum MessageType {
    TEXT,
    TEMPLATE,
    FLOW
  }
}
