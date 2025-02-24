package com.yuga.spring_rds.model.whatsapp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppTemplateRequestModel {

  private String name;
  private String category;
  private String language;

  @JsonProperty("components")
  private List<TemplateComponent> components;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TemplateComponent {
    private String type;
    private List<Parameter> parameters;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Parameter {
    private String type;
    private String text;
  }
}
