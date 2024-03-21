package org.prgms.locomocoserver.user.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.TestFactory;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.domain.enums.Provider;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private final TestFactory factory = new TestFactory();

    @Test
    @DisplayName("로그인 시도했을 때 최초의 user 정보를 가진 사용자가 생성된다.")
    void success_create_user() {
        //given when
        User user1 = factory.createKakaoUser();
        User user2 = factory.createGithubUser();

        //then
        assertThat(user1.getProvider()).isEqualTo(Provider.KAKAO.toString());
        assertThat(user2.getProvider()).isEqualTo(Provider.GITHUB.toString());
    }

    @Test
    @DisplayName("최초 회원가입 시 정보 누락이 있으면 안된다.")
    void success_init_userInfo() {
        //given
        User user = factory.createKakaoUser();

        //when
        user.setInitInfo("nickname", LocalDate.of(2002,2,25), Gender.FEMALE, factory.createJobTag("ETC"));

        //then
        assertThat(user.getBirth()).isNotNull();
        assertThat(user.getNickname()).isNotNull();
        assertThat(user.getGender()).isNotNull();
        assertThat(user.getJobTag()).isNotNull();
    }

    @Test
    @DisplayName("사용자의 개인정보를 수정할 수 있다.")
    void success_update_userInfo() {
        //given
        User user = factory.createFullUser();

        //when
        user.updateUserInfo(null, null, Gender.MALE, factory.createJobTag("DEVELOPER"));

        //then
        assertThat(user.getNickname()).isEqualTo("nickname");
        assertThat(user.getBirth()).isEqualTo("2002-02-25");
        assertThat(user.getGender()).isEqualTo(Gender.MALE);
        assertThat(user.getJobTag().getName()).isEqualTo("DEVELOPER");
    }

    @Test
    @DisplayName("사용자의 프로필 이미지를 업데이트 할 수 있다.")
    void success_update_profileImage() {
        //given
        User user = factory.createFullUser();

        //when
        user.updateProfileImage(factory.createImage("new profile image"));

        //then
        assertThat(user.getProfileImage().getPath()).isEqualTo("new profile image");
    }
}
