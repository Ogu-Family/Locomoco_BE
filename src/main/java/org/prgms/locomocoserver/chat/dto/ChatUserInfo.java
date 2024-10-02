package org.prgms.locomocoserver.chat.dto;

import org.prgms.locomocoserver.user.domain.User;

public record ChatUserInfo(
        Long userId,
        String nickname,
        String senderImage
) {
    public static ChatUserInfo of(User user) {
        String profileImage = user.getProfileImage()== null ? null : user.getProfileImage().getPath();
        return new ChatUserInfo(user.getId(), user.getNickname(), profileImage);
    }

    public static ChatUserInfo deletedUser(Long userId) {
        return new ChatUserInfo(userId, "(정보없음)", null);
    }
}
