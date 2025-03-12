package com.yuga.spring_rds.domain.whatsapp.messageRequestType.interactive;

import com.yuga.spring_rds.domain.whatsapp.ChatbotMessage;
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
public class InteractiveReplyButtonsMessageRequest extends InteractiveMessageRequest {
  ChatbotMessage header;
  Text body;
  Text footer;
  InteractiveAction action;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class InteractiveAction {
    List<InteractiveReplyButton> buttons;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class InteractiveReplyButton {
    Reply reply;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Reply {
    String id;
    String title;
  }
}
