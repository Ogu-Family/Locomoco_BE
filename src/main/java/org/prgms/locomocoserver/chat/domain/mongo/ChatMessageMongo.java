package org.prgms.locomocoserver.chat.domain.mongo;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document
public class ChatMessageMongo {

    @Id
    private String id;
    private String senderId;
    private String senderNickname;
    private String senderImage;
    private String message;
    private long timestamp;

    @Builder
    public ChatMessageMongo(String senderId, String senderNickname, String senderImage, String message, long timestamp) {
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.senderImage = senderImage;
        this.message = message;
        this.timestamp = timestamp;
    }
}
