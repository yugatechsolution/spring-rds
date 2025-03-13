package com.yuga.spring_rds.domain.whatsapp;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.yuga.spring_rds.domain.User;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.*;
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

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.EXISTING_PROPERTY,
      property = "type",
      visible = true)
  @JsonSubTypes({
    @JsonSubTypes.Type(value = TextMessageRequest.class, name = "TEXT"),
    @JsonSubTypes.Type(value = InteractiveMessageRequest.class, name = "INTERACTIVE"),
    @JsonSubTypes.Type(value = ImageMessageRequest.class, name = "IMAGE"),
    @JsonSubTypes.Type(value = DocumentMessageRequest.class, name = "DOCUMENT"),
    @JsonSubTypes.Type(value = VideoMessageRequest.class, name = "VIDEO")
  })
  @Convert(converter = MessageRequestConverter.class)
  private MessageRequest request;

  @OneToMany(mappedBy = "parentMessage", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<NextMessageMapping> nextMessages = new ArrayList<>();
}
