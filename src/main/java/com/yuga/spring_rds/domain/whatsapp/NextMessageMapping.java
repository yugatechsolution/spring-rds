package com.yuga.spring_rds.domain.whatsapp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "next_message_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NextMessageMapping {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "parent_message_id", nullable = false)
  private ChatbotMessage parentMessage;

  @ManyToOne
  @JoinColumn(name = "next_message_id")
  private ChatbotMessage nextMessage;

  @Column(nullable = false)
  private String actionTrigger; // Example: "BUTTON_1", "LIST_ITEM_2"
}
