package org.prgms.locomocoserver.chat.domain.mongo;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Document(collection = "chat_messages")
public class ChatMessageMongo {

    @Id
    private String id;
    @Indexed
    private String chatRoomId;
    private String senderId;
    private String message;
    private List<String> imageUrls;
    private boolean isNotice;
    @Indexed
    private LocalDateTime createdAt;

    @Builder
    public ChatMessageMongo(String chatRoomId, String senderId, String message, List<String> imageUrls, boolean isNotice, LocalDateTime createdAt) {
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.message = message;
        this.imageUrls = imageUrls;
        this.isNotice = isNotice;
        this.createdAt = createdAt;
    }

    public void validate() {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
    }
}
