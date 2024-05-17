package org.prgms.locomocoserver.mogakkos.domain.participants;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ParticipantRepositoryTest {

    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private MogakkoRepository mogakkoRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        participantRepository.deleteAll();
        mogakkoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("모각코 id와 유저 id를 이용해 특정 유저가 특정 모각코에 참여 중인지 확인할 수 있다")
    void success_find_by_mogakko_and_user() {
        // given
        User creator = User.builder().email("creator@gmail.com").nickname("creator").temperature(36.5)
            .birth(LocalDate.EPOCH).gender(Gender.MALE).provider("kakao")
            .build();
        User user = User.builder().email("email@gmail.com").nickname("temp").temperature(36.5)
            .birth(LocalDate.EPOCH).gender(Gender.MALE).provider("kakao")
            .build();
        LocalDateTime startTime = LocalDateTime.now();
        Mogakko mogakko = Mogakko.builder().title("title").startTime(startTime)
            .endTime(startTime.plusHours(2)).deadline(startTime.plusMinutes(30)).content("content")
            .maxParticipants(8).likeCount(0).creator(creator)
            .build();
        Participant participant = Participant.builder().mogakko(mogakko).user(user)
            .build();
        mogakko.addParticipant(participant);

        userRepository.saveAll(List.of(creator, user));
        mogakkoRepository.save(mogakko);
        participantRepository.save(participant);

        // when
        Optional<Participant> retrievedParticipant = participantRepository.findByMogakkoIdAndUserId(
            mogakko.getId(), user.getId());

        assertThat(retrievedParticipant.isPresent()).isTrue();
        assertThat(retrievedParticipant.get().getUser()).isSameAs(user);

    }
}
