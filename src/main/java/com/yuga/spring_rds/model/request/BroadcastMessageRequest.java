package com.yuga.spring_rds.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BroadcastMessageRequest {
  private List<String> phoneNumbers;
  private RequestType requestType;
  private TemplateMessageRequest templateMessageRequest;
  private TextMessageRequest textMessageRequest;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TextMessageRequest {
    private String textBody;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TemplateMessageRequest {
    private String templateName;
  }

  public enum RequestType {
    TEXT,
    TEMPLATE
  }
}
