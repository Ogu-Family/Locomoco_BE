package org.prgms.locomocoserver.chat.dto;

import org.prgms.locomocoserver.chat.domain.ChatMessage;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongo;
import org.prgms.locomocoserver.user.domain.User;

import java.time.LocalDateTime;

public record ChatMessageDto(
        String chatMessageId,
        String chatRoomId,
        String senderId,
        String senderNickName,
        String senderProfileImage,
        String message,
        boolean isNotice,
        LocalDateTime createdAt
) {
    public static ChatMessageDto of(ChatMessage chatMessage) {
        User sender = chatMessage.getSender();
        String profileImagePath = null;
        if (sender.getProfileImage() != null) {
            profileImagePath = sender.getProfileImage().getPath();
        }
        return new ChatMessageDto(chatMessage.getId().toString(), chatMessage.getChatRoom().getId().toString(), sender.getId().toString(),
                sender.getNickname(), profileImagePath, chatMessage.getContent(), chatMessage.isNotice(), chatMessage.getCreatedAt());
    }

    public static ChatMessageDto of(Long chatRoomId, ChatMessageMongo chatMessageMongo) {
        String profileImagePath = null;
        if (chatMessageMongo.getSenderImage() != null) {
            profileImagePath = chatMessageMongo.getSenderImage();
        }
        return new ChatMessageDto(chatMessageMongo.getId(), chatRoomId.toString(), chatMessageMongo.getSenderId(),
                chatMessageMongo.getSenderNickname(), profileImagePath, chatMessageMongo.getMessage(), chatMessageMongo.isNotice(), chatMessageMongo.getCreatedAt());
    }
}

