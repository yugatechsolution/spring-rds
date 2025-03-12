package com.yuga.spring_rds.domain.whatsapp.messageRequestType;

import com.yuga.spring_rds.domain.whatsapp.util.Text;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextMessageRequest extends Text implements MessageRequest {
  boolean previewUrl = true;
}
