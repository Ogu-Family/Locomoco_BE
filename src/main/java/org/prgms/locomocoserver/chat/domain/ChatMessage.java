package org.prgms.locomocoserver.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.global.common.BaseEntity;

@Entity
@Getter
@Table(name = "chat_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @Column(name = "content")
    private String content;

    @Builder
    public ChatMessage(Long senderId, Long chatRoomId, String content) {
        this.senderId = senderId;
        this.chatRoomId = chatRoomId;
        this.content = content;
    }
}
