package org.prgms.locomocoserver.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.global.TestFactory;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.exception.UserException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("사용자 초기 정보 저장 시 닉네임 검증이 필요합니다")
    void validUserNickName() {
        // given
        String nickname = "엑";
        LocalDate birth = LocalDate.of(2020, 1, 1);
        Gender gender = Gender.MALE;

        // when
        User user = TestFactory.createUser();

        // then
        assertThrows(UserException.class, () -> {
            user.setInitInfo(nickname, birth, gender, null);
        });
    }

    @Test
    @DisplayName("사용자 초기 정보 저장 시 생년월일 검증이 필요합니다")
    void validUserBirth() {
        // given
        String nickname = "test";
        LocalDate birth = LocalDate.of(2020, 1, 1);
        Gender gender = Gender.MALE;

        // when
        User user = TestFactory.createUser();

        // then
        assertThrows(UserException.class, () -> {
            user.setInitInfo(nickname, birth, gender, null);
        });
    }
}