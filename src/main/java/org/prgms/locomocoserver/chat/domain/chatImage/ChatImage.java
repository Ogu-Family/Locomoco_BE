package org.prgms.locomocoserver.chat.domain.chatImage;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatMessage;
import org.prgms.locomocoserver.image.domain.Image;

@Entity
@Getter
@Table(name = "chat_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_message_id")
    private ChatMessage chatMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @Builder
    public ChatImage(ChatMessage chatMessage, Image image) {
        this.chatMessage = chatMessage;
        this.image = image;
    }

}
