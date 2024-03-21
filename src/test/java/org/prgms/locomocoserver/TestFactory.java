package org.prgms.locomocoserver;

import org.prgms.locomocoserver.categories.domain.Category;
import org.prgms.locomocoserver.categories.domain.CategoryInputType;
import org.prgms.locomocoserver.categories.domain.CategoryType;
import org.prgms.locomocoserver.image.domain.Image;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.domain.enums.Provider;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TestFactory {

    public User createKakaoUser() {
        return User.builder()
                .email("example@example.com")
                .provider(Provider.KAKAO.name())
                .temperature(36.5).build();
    }

    public User createGithubUser() {
        return User.builder()
                .email("example@example.com")
                .provider(Provider.GITHUB.name())
                .temperature(36.5).build();
    }

    public User createFullUser() {
        return User.builder()
                .nickname("nickname")
                .birth(LocalDate.of(2002,2,25))
                .gender(Gender.FEMALE)
                .temperature(36.5)
                .provider(Provider.KAKAO.name())
                .jobTag(createJobTag("ETC"))
                .profileImage(createImage("profile image")).build();
    }

    public Tag createJobTag(String name) {
        return Tag.builder()
                .name(name)
                .category(createCategory("JOB"))
                .build();
    }

    public Image createImage(String path) {
        return Image.builder()
                .key("key")
                .path(path)
                .build();
    }

    public Category createCategory(String name) {
        return Category.builder()
                .name(name)
                .categoryInputType(CategoryInputType.RADIOGROUP)
                .categoryType(CategoryType.USER)
                .build();
    }
}
