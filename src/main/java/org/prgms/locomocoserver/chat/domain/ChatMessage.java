package org.prgms.locomocoserver.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.global.common.BaseEntity;
import org.prgms.locomocoserver.user.domain.User;

@Entity
@Getter
@Table(name = "chat_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_rooms_id")
    private ChatRoom chatRoom;

    @Column(name = "content")
    private String content;

    @Column(name = "isNotice")
    private boolean isNotice;

    @Builder
    public ChatMessage(User sender, ChatRoom chatRoom, String content, boolean isNotice) {
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.content = content;
        this.isNotice = isNotice;
    }
}
