package com.yuga.spring_rds.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NextMessageMappingDTO {
  private Integer parentMessageIndex;

  @JsonAlias("childMessageIndex")
  private Integer nextMessageIndex;

  private String actionTrigger;
}
