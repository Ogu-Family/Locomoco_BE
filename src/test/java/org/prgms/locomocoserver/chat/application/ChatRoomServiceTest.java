package org.prgms.locomocoserver.chat.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.dto.request.ChatEnterRequestDto;
import org.prgms.locomocoserver.location.domain.Location;
import org.prgms.locomocoserver.location.dto.LocationInfoDto;
import org.prgms.locomocoserver.mogakkos.application.MogakkoService;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoCreateRequestDto;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.domain.enums.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@SpringBootTest
class ChatRoomServiceTest {

    @Autowired
    private PlatformTransactionManager tx;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MogakkoService mogakkoService;
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Test
    @DisplayName("채팅방에 참여할 수 있다")
    void success_enter_chat_room_as_participant() {
        // given
        List<User> dummyUsers = new ArrayList<>();
        IntStream.rangeClosed(0, 2).forEach(i -> dummyUsers.add(
            User.builder().nickname("name" + i).email(i + "email@gmail.com").birth(LocalDate.EPOCH)
                .temperature(36.5).provider("kakao").gender(Gender.MALE).job(Job.ETC).build()));
        userRepository.saveAll(dummyUsers);

        Location location = Location.builder().city("Carry You").address("Martin Garrix")
            .latitude(10.233214).longitude(23.312314).build();
        Long mogakkoId = mogakkoService.save(
            new MogakkoCreateRequestDto(dummyUsers.get(0).getId(), "title", LocationInfoDto.create(location),
                LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(1),
                10, "", List.of()));
        Mogakko mogakko = mogakkoService.getByIdNotDeleted(mogakkoId);

        // when
        chatRoomService.enterChatRoom(new ChatEnterRequestDto(mogakko.getChatRoom().getId(), dummyUsers.get(1)));
        chatRoomService.enterChatRoom(new ChatEnterRequestDto(mogakko.getChatRoom().getId(), dummyUsers.get(2)));

        // then
        tx.getTransaction(new DefaultTransactionDefinition());

        ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedAtIsNull(
            mogakko.getChatRoom().getId()).orElseThrow(RuntimeException::new);

        assertThat(chatRoom.getMogakko().getId()).isEqualTo(mogakkoId);
        assertThat(chatRoom.getCreator().getId()).isEqualTo(dummyUsers.get(0).getId());
        assertThat(chatRoom.getParticipants()).hasSize(2);
    }
}
