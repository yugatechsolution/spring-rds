package com.yuga.spring_rds.model.whatsapp.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.yuga.spring_rds.domain.whatsapp.WhatsAppMessageType;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.TextMessageRequest;
import com.yuga.spring_rds.domain.whatsapp.messageRequestType.VideoMessageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
public class WhatsAppMessageRequestModelTest {

  @Autowired private JacksonTester<WhatsAppMessageRequestModel> json;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    objectMapper = JsonMapper.builder().findAndAddModules().build();
  }

  @Test
  void shouldDeserializeTextMessageRequest() throws Exception {
    // language=JSON
    String jsonContent =
        """
                {
                  "messaging_product": "whatsapp",
                  "recipient_type": "individual",
                  "to": "123456789",
                  "type": "text",
                  "text": {
                    "body": "Hello from WhatsApp"
                  }
                }
                """;

    WhatsAppMessageRequestModel result =
        objectMapper.readValue(jsonContent, WhatsAppMessageRequestModel.class);

    assertThat(result.getMessagingProduct()).isEqualTo("whatsapp");
    assertThat(result.getRecipientType()).isEqualTo("individual");
    assertThat(result.getTo()).isEqualTo("123456789");
    assertThat(result.getType()).isEqualTo(WhatsAppMessageType.text);

    // Verify polymorphic deserialization
    assertThat(result.getText()).isNotNull();
    TextMessageRequest request = result.getText();
    assertThat(request.getBody()).isEqualTo("Hello from WhatsApp");
  }

  @Test
  void shouldDeserializeAudioMessageRequest() throws Exception {
    // language=JSON
    String jsonContent =
        """
                {
                  "messaging_product": "whatsapp",
                  "recipient_type": "individual",
                  "to": "123456789",
                  "type": "video",
                  "video": {
                    "link": "https://example.com/audio.mp3"
                  }
                }
                """;

    WhatsAppMessageRequestModel result =
        objectMapper.readValue(jsonContent, WhatsAppMessageRequestModel.class);

    assertThat(result.getMessagingProduct()).isEqualTo("whatsapp");
    assertThat(result.getRecipientType()).isEqualTo("individual");
    assertThat(result.getTo()).isEqualTo("123456789");
    assertThat(result.getType()).isEqualTo(WhatsAppMessageType.video);

    // Verify polymorphic deserialization
    assertThat(result.getVideo()).isNotNull();
    VideoMessageRequest request = result.getVideo();
    assertThat(request.getLink()).isEqualTo("https://example.com/audio.mp3");
  }

  @Test
  void shouldSerializeTextMessageRequest() throws Exception {
    TextMessageRequest testRequest =
        TextMessageRequest.builder().body("Hello from WhatsApp").build();

    WhatsAppMessageRequestModel model =
        WhatsAppMessageRequestModel.builder()
            .messagingProduct("whatsapp")
            .recipientType("individual")
            .to("123456789")
            .type(WhatsAppMessageType.text)
            .text(testRequest)
            .build();

    String jsonResult = objectMapper.writeValueAsString(model);

    assertThat(jsonResult).contains("\"type\":\"text\"");
    assertThat(jsonResult).contains("\"body\":\"Hello from WhatsApp\"");
  }

  @Test
  void shouldSerializeAudioMessageRequest() throws Exception {
    VideoMessageRequest audioRequest =
        VideoMessageRequest.builder().link("https://example.com/audio.mp3").build();

    WhatsAppMessageRequestModel model =
        WhatsAppMessageRequestModel.builder()
            .messagingProduct("whatsapp")
            .recipientType("individual")
            .to("123456789")
            .type(WhatsAppMessageType.video)
            .video(audioRequest)
            .build();

    String jsonResult = objectMapper.writeValueAsString(model);

    assertThat(jsonResult).contains("\"type\":\"video\"");
    assertThat(jsonResult).contains("\"link\":\"https://example.com/audio.mp3\"");
  }
}
