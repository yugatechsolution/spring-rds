package com.yuga.spring_rds.domain.whatsapp.messageRequestType.interactive;

import com.yuga.spring_rds.domain.whatsapp.messageRequestType.InteractiveMessageRequest;
import com.yuga.spring_rds.domain.whatsapp.util.Text;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InteractiveCtaUrlMessageRequest extends InteractiveMessageRequest {
  Header header;
  Text body;
  Text footer;
  InteractiveAction action;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class InteractiveAction {
    String name = "cta_url";
    Parameters parameters;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Parameters {
    String displayText;
    String url;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Header {
    String type = "text";
    String text;
  }
}
