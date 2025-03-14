package com.yuga.spring_rds.model.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuga.spring_rds.domain.whatsapp.util.BaseWhatsAppMessageRequest;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhatsAppMessageRequest extends BaseWhatsAppMessageRequest {

  private Long id;
  private List<String> phoneNumbers;
}
