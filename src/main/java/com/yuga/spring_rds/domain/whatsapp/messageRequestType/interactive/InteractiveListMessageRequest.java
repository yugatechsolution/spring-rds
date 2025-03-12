package com.yuga.spring_rds.domain.whatsapp.messageRequestType.interactive;

import com.yuga.spring_rds.domain.whatsapp.messageRequestType.InteractiveMessageRequest;
import com.yuga.spring_rds.domain.whatsapp.util.Text;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InteractiveListMessageRequest extends InteractiveMessageRequest {
  Header header;
  Text body;
  Text footer;
  Action action;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Action {
    List<Section> sections;
    String button;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Section {
    String title;
    List<Row> rows;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Row {
    String id;
    String title;
    String description;
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
