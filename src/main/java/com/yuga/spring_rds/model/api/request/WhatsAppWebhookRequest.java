package com.yuga.spring_rds.model.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuga.spring_rds.domain.whatsapp.WhatsAppMessageType;
import com.yuga.spring_rds.model.whatsapp.reply.InteractiveReply;
import com.yuga.spring_rds.model.whatsapp.reply.NonInteractiveReply;
import java.util.List;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhatsAppWebhookRequest {
  private String object;
  private List<Entry> entry;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Entry {
    private String id;
    private List<Change> changes;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Change {
    private String field;
    private Value value;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Value {
    private String messagingProduct;
    private Metadata metadata;
    private List<Contact> contacts;
    private List<Message> messages;
    private List<Status> statuses;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Metadata {
    private String displayPhoneNumber;
    private String phoneNumberId;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Contact {
    private Profile profile;
    private String waId;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Profile {
    private String name;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Message {
    private String from;
    private String id;
    private NonInteractiveReply.TextReply text;
    private NonInteractiveReply.AudioReply audio;
    private NonInteractiveReply.VideoReply video;
    private NonInteractiveReply.ImageReply image;
    private NonInteractiveReply.DocumentReply document;
    private InteractiveReply interactive;
    private String timestamp;
    private WhatsAppMessageType type;
    private Context context;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Context {
    private String from;
    private String id;
  }

  // ✅ Added Status class
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Status {
    private String id;
    private String recipientId;
    private String status; // delivered, read, failed, sent
    private Long timestamp;
    private Conversation conversation;
    private Pricing pricing;
    private Error error;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Conversation {
      private String id;
      private String originType; // user_initiated, business_initiated, referral_conversion
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pricing {
      private String pricingModel; // conversation-based
      private String category; // marketing, utility, authentication
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Error {
      private int code;
      private String title;
      private String message;
    }
  }
}
