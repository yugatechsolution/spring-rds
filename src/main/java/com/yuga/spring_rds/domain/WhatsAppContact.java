package com.yuga.spring_rds.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "whatsapp_contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(WhatsAppContactId.class)
public class WhatsAppContact {

  @Id
  @Column(name = "phone_number_id", nullable = false)
  private String phoneNumberId; // Business Phone Number ID

  @Id
  @Column(name = "wa_id", nullable = false)
  private String waId; // Unique WhatsApp User ID

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "registered_at", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @Builder.Default
  private java.util.Date registeredAt = new java.util.Date();
}
