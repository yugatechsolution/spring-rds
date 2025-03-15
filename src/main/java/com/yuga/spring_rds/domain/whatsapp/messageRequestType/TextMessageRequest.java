package com.yuga.spring_rds.domain.whatsapp.messageRequestType;

import com.yuga.spring_rds.domain.whatsapp.util.Body;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TextMessageRequest extends Body implements MessageRequest {
  boolean previewUrl = true;
}
