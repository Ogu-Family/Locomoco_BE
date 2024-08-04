package org.prgms.locomocoserver.chat.domain.mongo;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Document
public class ChatMessageMongo {

    @Id
    private String id;
    private String senderId;
    private String senderNickname;
    private String senderImage;
    private String message;
    private List<String> imageUrls;
    private boolean isNotice;
    private LocalDateTime createdAt;

    @Builder
    public ChatMessageMongo(String senderId, String senderNickname, String senderImage, String message, List<String> imageUrls, boolean isNotice, LocalDateTime createdAt) {
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.senderImage = senderImage;
        this.message = message;
        this.imageUrls = imageUrls;
        this.isNotice = isNotice;
        this.createdAt = createdAt;
    }
}
