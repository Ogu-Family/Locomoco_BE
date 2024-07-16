package org.prgms.locomocoserver.global;

import org.prgms.locomocoserver.categories.domain.Category;
import org.prgms.locomocoserver.categories.domain.CategoryInputType;
import org.prgms.locomocoserver.categories.domain.CategoryType;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.image.domain.Image;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.domain.enums.Provider;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class TestFactory {

    public static User createUser() {
        return User.builder()
                .nickname("test")
                .birth(LocalDate.now())
                .gender(Gender.MALE)
                .temperature(36.5).email("test@example.com")
                .profileImage(createImage())
                .jobTag(null)
                .provider(Provider.KAKAO.name())
                .build();
    }

    public static Image createImage() {
        return Image.builder()
                .key("image key")
                .path("image url").build();
    }

    public static Tag createTag(String tagName, String categoryName, CategoryType categoryType, CategoryInputType categoryInputType) {
        return Tag.builder()
                .category(createCategory(categoryName, categoryType, categoryInputType))
                .name(tagName)
                .build();
    }

    public static Category createCategory(String name, CategoryType categoryType, CategoryInputType categoryInputType) {
        return Category.builder()
                .name(name)
                .categoryType(categoryType)
                .categoryInputType(categoryInputType)
                .build();
    }

    public static ChatRoom createChatRoom(User creator, Mogakko mogakko) {
        return ChatRoom.builder()
                .creator(creator)
                .mogakko(mogakko)
                .name("test chat room")
                .chatParticipants(null)
                .build();
    }

    public static Mogakko createMogakko(User creator) {
        return Mogakko.builder()
                .creator(creator)
                .title("test mogakko")
                .content("test mogakko content")
                .mogakkoTags(null)
                .startTime(LocalDateTime.now().plusDays(3))
                .endTime(LocalDateTime.now().plusDays(3).plusHours(2))
                .deadline(LocalDateTime.now().plusDays(1))
                .maxParticipants(5)
                .likeCount(0)
                .build();
    }
}
