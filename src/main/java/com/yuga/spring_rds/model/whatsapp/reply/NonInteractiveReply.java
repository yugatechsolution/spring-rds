package com.yuga.spring_rds.model.whatsapp.reply;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class NonInteractiveReply {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class TextReply {
    private String body;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ImageReply {
    private String id;
    private String mimeType;
    private String sha256;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class DocumentReply {
    private String id;
    private String mimeType;
    private String sha256;
    private String filename;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class AudioReply {
    private String id;
    private String mimeType;
    private String sha256;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class VideoReply {
    private String id;
    private String mimeType;
    private String sha256;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class StickerReply {
    private String id;
    private String mimeType;
    private String sha256;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class LocationReply {
    private double latitude;
    private double longitude;
    private String name;
    private String address;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class TemplateReply {
    private String name;
    private List<Component> components;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Component {
      private String type;
      private List<Parameter> parameters;

      @Data
      @Builder
      @NoArgsConstructor
      @AllArgsConstructor
      @JsonIgnoreProperties(ignoreUnknown = true)
      public static class Parameter {
        private String type;
        private String text;
      }
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ReactionReply {
    private String emoji;
    private String messageId;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ButtonReply {
    private String payload;
    private String text;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ContactsReply {
    private List<Contact> contacts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Contact {
      private String name;
      private String phoneNumber;
      private String email;
      private String address;
    }
  }
}
