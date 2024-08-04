package org.prgms.locomocoserver.chat.dto;

import org.prgms.locomocoserver.chat.domain.ChatMessage;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongo;
import org.prgms.locomocoserver.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageDto(
        String chatMessageId,
        String chatRoomId,
        String senderId,
        String senderNickName,
        String senderProfileImage,
        String message,
        List<String> imageUrls,
        boolean isNotice,
        LocalDateTime createdAt
) {
    public static ChatMessageDto of(ChatMessage chatMessage) {
        User sender = chatMessage.getSender();
        String profileImagePath = sender.getProfileImage() == null ? null : sender.getProfileImage().getPath();

        return new ChatMessageDto(chatMessage.getId().toString(), chatMessage.getChatRoom().getId().toString(), sender.getId().toString(),
                sender.getNickname(), profileImagePath, chatMessage.getContent(), null, chatMessage.isNotice(), chatMessage.getCreatedAt());
    }

    public static ChatMessageDto of(Long chatRoomId, ChatMessageMongo chatMessageMongo) {
        String profileImagePath = chatMessageMongo.getSenderImage() == null ? null : chatMessageMongo.getSenderImage();
        return new ChatMessageDto(chatMessageMongo.getId(), chatRoomId.toString(), chatMessageMongo.getSenderId(),
                chatMessageMongo.getSenderNickname(), profileImagePath, chatMessageMongo.getMessage(), null, chatMessageMongo.isNotice(), chatMessageMongo.getCreatedAt());
    }

    public static ChatMessageDto of(Long chatRoomId, List<String> imageUrls, ChatMessageMongo chatMessageMongo) {
        String profileImagePath = chatMessageMongo.getSenderImage() == null? null : chatMessageMongo.getSenderImage();
        return new ChatMessageDto(chatMessageMongo.getId(), chatRoomId.toString(), chatMessageMongo.getSenderId(),
                chatMessageMongo.getSenderNickname(), profileImagePath, chatMessageMongo.getMessage(), imageUrls, chatMessageMongo.isNotice(), chatMessageMongo.getCreatedAt());
    }
}

