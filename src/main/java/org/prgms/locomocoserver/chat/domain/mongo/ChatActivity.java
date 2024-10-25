package org.prgms.locomocoserver.chat.domain.mongo;

import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "chat_activity")
@CompoundIndex(def = "{'userId': 1, 'chatRoomId': 1}", name = "user_chatRoom_idx", unique = true)
public class ChatActivity {
    @Id
    private String id;
    private String userId;
    private String chatRoomId;
    private ObjectId lastReadMsgId;
    private long unReadMsgCnt;

    @Builder
    public ChatActivity(String userId, String chatRoomId) {
        this.userId = userId;
        this.chatRoomId = chatRoomId;
        this.lastReadMsgId = null;
        this.unReadMsgCnt = 0;
    }

    public void updateLastReadMessage(String userId, ObjectId lastReadMsgId) {
        if (!this.userId.equals(userId)) {
            throw new IllegalArgumentException("Invalid User Id : " + userId);
        }
        this.lastReadMsgId = lastReadMsgId;
        this.unReadMsgCnt = 0;
    }

    public void increaseUnreadMsgCnt() {
        this.unReadMsgCnt++;
    }
}
