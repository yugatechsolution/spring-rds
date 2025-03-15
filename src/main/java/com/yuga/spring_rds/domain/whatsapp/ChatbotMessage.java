package com.yuga.spring_rds.domain.whatsapp;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.yuga.spring_rds.domain.User;
import com.yuga.spring_rds.util.MessageRequestConverter;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chatbot_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedEntityGraph(
    name = "ChatbotMessage.fullFlow",
    attributeNodes = {
      @NamedAttributeNode(value = "nextMessages", subgraph = "nextMessagesSubgraph")
    },
    subgraphs = {
      @NamedSubgraph(
          name = "nextMessagesSubgraph",
          attributeNodes = {@NamedAttributeNode("nextMessage")})
    })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ChatbotMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user; // Foreign key to User entity

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private WhatsAppMessageType type;

  @Convert(converter = MessageRequestConverter.class)
  @Column(columnDefinition = "JSON")
  private JsonNode request;

  @OneToMany(mappedBy = "parentMessage", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<NextMessageMapping> nextMessages = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "trigger_id")
  private ChatbotTrigger trigger;
}
