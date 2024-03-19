package org.prgms.locomocoserver.mogakkos.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.enums.Gender;

class MogakkoTest {
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder().email("email@gmail.com").birth(LocalDate.EPOCH)
            .nickname("nickname").gender(
                Gender.MALE).build();
    }

    @Test
    @DisplayName("정상적으로 모각코를 생성한다")
    void success_create_Mogakko() {
        // given when
        Mogakko mogakko1 = Mogakko.builder().title("title").content("content").views(1L).likeCount(0)
            .startTime(LocalDateTime.now()).deadline(LocalDateTime.now()).endTime(LocalDateTime.now())
            .maxParticipants(10).creator(testUser).build();
        Mogakko mogakko2 = Mogakko.builder().title(" ").content("content").views(1L).likeCount(0)
            .startTime(LocalDateTime.now()).deadline(LocalDateTime.now()).endTime(LocalDateTime.now())
            .maxParticipants(10).creator(testUser).build();
        // then
        assertThat(mogakko1).isNotNull();
        assertThat(mogakko2.getTitle()).isEqualTo(Mogakko.DEFAULT_TITLE);
    }

    @Test
    @DisplayName("시작 시간이 끝 시간보다 뒤에 있는 모각코는 생성할 수 없다")
    void fail_create_Mogakko_given_startTime_is_after_endTime() {
        // given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.minusHours(3);
        LocalDateTime deadline = startTime.plusHours(2);

        // when then
        assertThrows(RuntimeException.class, () -> Mogakko.builder().title("title").content("content").views(1L).likeCount(0) // TODO: 모각코 예외 반환
            .startTime(startTime).deadline(deadline).endTime(endTime)
            .maxParticipants(10).creator(testUser).build());
    }

    @Test
    @DisplayName("데드 라인이 끝 시간 뒤에 존재하는 모각코는 생성할 수 없다")
    void fail_create_Mogakko_given_deadline_is_not_between_endTime_and_startTime() {
        // given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(2);
        LocalDateTime deadline = startTime.plusHours(3);

        // when then
        assertThrows(RuntimeException.class, () -> Mogakko.builder().title("title").content("content").views(1L).likeCount(0) // TODO: 모각코 예외 반환
            .startTime(startTime).deadline(deadline).endTime(endTime)
            .maxParticipants(10).creator(testUser).build());
    }

    @Test
    @DisplayName("255자보다 많은 사이즈의 제목으로 된 모각코를 생성할 수 없다")
    void fail_create_Mogakko_given_title_length_is_bigger_than_255() {
        // given
        String title = "a".repeat(256);

        // when then
        assertThrows(RuntimeException.class, () -> Mogakko.builder().title(title).content("content").views(1L).likeCount(0) // TODO: 모각코 예외 반환
            .startTime(LocalDateTime.now()).deadline(LocalDateTime.now()).endTime(LocalDateTime.now())
            .maxParticipants(10).creator(testUser).build());
    }

    @Test
    @DisplayName("500자보다 많은 사이즈의 내용으로 된 모각코를 생성할 수 없다")
    void fail_create_Mogakko_given_content_length_is_bigger_than_500() {
        // given
        String content = "a".repeat(501);

        // when then
        assertThrows(RuntimeException.class, () -> Mogakko.builder().title("title").content(content).views(1L).likeCount(0) // TODO: 모각코 예외 반환
            .startTime(LocalDateTime.now()).deadline(LocalDateTime.now()).endTime(LocalDateTime.now())
            .maxParticipants(10).creator(testUser).build());
    }

    @Test
    @DisplayName("잘못된 최대 인원 수를 가진 모각코를 생성할 수 없다")
    void fail_create_Mogakko_given_max_participants_incorrect() {
        // given
        int maxParticipants = 25;

        // when then
        assertThrows(RuntimeException.class, () -> Mogakko.builder().title("title").content("content").views(1L).likeCount(0) // TODO: 모각코 예외 반환
            .startTime(LocalDateTime.now()).deadline(LocalDateTime.now()).endTime(LocalDateTime.now())
            .maxParticipants(maxParticipants).creator(testUser).build());
    }

    @Test
    @DisplayName("모각코 정보를 업데이트할 수 있다")
    void success_update_Mogakko_info() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Mogakko mogakko = Mogakko.builder().title("title").content("content").views(1L).likeCount(0)
            .startTime(now).deadline(now).endTime(now)
            .maxParticipants(10).creator(testUser).build();

        String updateTitle = "updateTitle";
        int updateMaxParticipants = 4;
        String updateContent = "updateContent";
        LocalDateTime updateStartTime = LocalDateTime.now();
        LocalDateTime updateEndTime = now.plusHours(1);
        LocalDateTime updateDeadline = now.plusMinutes(30);
        // when
        mogakko.updateInfo(updateTitle, updateContent, updateStartTime, updateEndTime, updateDeadline,
            updateMaxParticipants);

        // then
        assertThat(mogakko.getTitle()).isEqualTo(updateTitle);
        assertThat(mogakko.getContent()).isEqualTo(updateContent);
        assertThat(mogakko.getStartTime()).isEqualTo(updateStartTime);
        assertThat(mogakko.getEndTime()).isEqualTo(updateEndTime);
        assertThat(mogakko.getDeadline()).isEqualTo(updateDeadline);
        assertThat(mogakko.getMaxParticipants()).isEqualTo(updateMaxParticipants);
    }
}
