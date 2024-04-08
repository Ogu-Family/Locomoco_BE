package org.prgms.locomocoserver.global.common;

import static com.slack.api.webhook.WebhookPayloads.payload;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.global.common.dto.SlackErrorAlertDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SlackAlertService {
    private static final String RED = "#ff0000";

    private final Slack slackClient = Slack.getInstance();

    @Value("${webhook.slack.url}")
    private String webhookUrl;

    public void sendAlertLog(SlackErrorAlertDto dto) {
        try {
            slackClient.send(webhookUrl, payload(p -> p
                .text("서버 에러 발생! 백엔드 측의 빠른 확인 요망")
                // attachment는 list 형태여야 합니다.
                .attachments(
                    List.of(generateErrorAttachment(dto))
                )
            ));
        } catch (IOException slackError) {
            log.info("Slack 통신과의 예외 발생");
        }
    }

    // attachment 생성 메서드
    private Attachment generateErrorAttachment(SlackErrorAlertDto dto) {
        return Attachment.builder()
            .color(RED)
            .title("에러 로그")
            .fields(List.of(
                    generateSlackField("Request URL", dto.requestUrl() + " " + dto.httpMethod()),
                    generateSlackField("Error Message", dto.errorMessage())
                )
            )
            .build();
    }

    private Field generateSlackField(String title, String value) {
        return Field.builder()
            .title(title)
            .value(value)
            .valueShortEnough(false)
            .build();
    }

}
