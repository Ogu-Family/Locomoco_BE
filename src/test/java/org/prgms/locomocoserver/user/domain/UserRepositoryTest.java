package org.prgms.locomocoserver.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.participants.Participant;
import org.prgms.locomocoserver.mogakkos.domain.participants.ParticipantRepository;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private MogakkoRepository mogakkoRepository;

    @Test
    @DisplayName("특정 모각코에 참여중인 인원 목록을 전부 가져올 수 있다.")
    void success_find_all_participants_by_mogakko() {
        // given
        LocalDateTime startTime = LocalDateTime.now();

        User user1 = User.builder().nickname("name").birth(startTime.toLocalDate())
            .email("email@gmail.com").gender(Gender.MALE).provider("provider").temperature(1).build();
        User user2 = User.builder().nickname("namae").birth(startTime.toLocalDate())
            .email("cho@gmail.com").gender(Gender.MALE).provider("provider").temperature(2).build();

        userRepository.saveAll(List.of(user1, user2));

        Mogakko mogakko = Mogakko.builder().title("title").content("content").startTime(
                startTime).endTime(startTime.plusHours(2)).deadline(startTime.plusHours(1))
            .likeCount(0).views(0).creator(user1).build();

        Participant participant1 = participantRepository.save(
            Participant.builder().user(user1).build());
        Participant participant2 = participantRepository.save(
            Participant.builder().user(user2).build());

        mogakko.addParticipant(participant1);
        mogakko.addParticipant(participant2);

        // when
        Mogakko savedMogakko = mogakkoRepository.save(mogakko);

        // then
        List<User> allParticipantsByMogakko = userRepository.findAllParticipantsByMogakko(
            savedMogakko);

        assertThat(allParticipantsByMogakko).hasSize(2);
    }
}
