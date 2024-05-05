package org.prgms.locomocoserver.chat.domain.mongo;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Document(collection = "chat_rooms")
public class ChatRoomMongo {

    @Id
    private String id;
    private List<ChatMessageMongo> messages;

    public void addChatMessage(ChatMessageMongo chatMessage) {
        if(chatMessage == null) throw new IllegalArgumentException();

        this.messages.add(chatMessage);
    }

    @Builder
    public ChatRoomMongo(List<ChatMessageMongo> messages) {
        this.messages = messages;
    }
}
