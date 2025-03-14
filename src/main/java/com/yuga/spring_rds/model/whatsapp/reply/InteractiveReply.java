package com.yuga.spring_rds.model.whatsapp.reply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractiveReply {

  private Type type;
  private ListReply listReply;
  private ButtonReply buttonReply;
  private FlowReply flowReply;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ListReply {
    private String id;
    private String title;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ButtonReply {
    private String id;
    private String title;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FlowReply {
    private String id;
    private String flowToken;
    private String button;
  }

  public enum Type {
    list_reply,
    button_reply,
    flow_reply
  }
}
