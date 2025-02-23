package com.yuga.spring_rds.model.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastMessageRequest {
  private List<String> phoneNumbers;
  private String message;
}
