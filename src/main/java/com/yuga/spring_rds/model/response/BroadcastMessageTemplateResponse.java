package com.yuga.spring_rds.model.response;

import com.yuga.spring_rds.util.Constants;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastMessageTemplateResponse {
  private Map<String, Constants.Status> statusMap;
}
