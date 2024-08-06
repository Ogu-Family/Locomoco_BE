package org.prgms.locomocoserver.chat.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.chat.domain.ChatMessageRepository;
import org.prgms.locomocoserver.chat.domain.ChatParticipantRepository;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.dto.request.ChatEnterRequestDto;
import org.prgms.locomocoserver.chat.exception.ChatErrorType;
import org.prgms.locomocoserver.chat.exception.ChatException;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocation;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocationRepository;
import org.prgms.locomocoserver.mogakkos.domain.vo.AddressInfo;
import org.prgms.locomocoserver.mogakkos.dto.LocationInfoDto;
import org.prgms.locomocoserver.mogakkos.application.MogakkoService;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.participants.ParticipantRepository;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoCreateResponseDto;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
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
    @Autowired
    private MogakkoRepository mogakkoRepository;
    @Autowired
    private ChatParticipantRepository chatParticipantRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private MogakkoLocationRepository mogakkoLocationRepository;
    @Autowired
    private ParticipantRepository participantRepository;

    @AfterEach
    void tearDown() {
        participantRepository.deleteAll();
        mogakkoLocationRepository.deleteAll();
        chatMessageRepository.deleteAll();
        chatParticipantRepository.deleteAll();
        chatRoomRepository.deleteAll();
        mogakkoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("채팅방에 참여할 수 있다")
    void success_enter_chat_room_as_participant() {
        // given
        List<User> dummyUsers = new ArrayList<>();
        IntStream.rangeClosed(0, 2).forEach(i -> dummyUsers.add(
            User.builder().nickname("name" + i).email(i + "email@gmail.com").birth(LocalDate.EPOCH)
                .temperature(36.5).provider("kakao").gender(Gender.MALE).build()));
        userRepository.saveAll(dummyUsers);

        AddressInfo addressInfo = AddressInfo.builder().address("Martin Garrix").city("Carry You")
            .build();

        MogakkoLocation mogakkoLocation = MogakkoLocation.builder().addressInfo(addressInfo)
            .latitude(10.233214).longitude(23.312314).build();
        MogakkoCreateResponseDto responseDto = mogakkoService.save(
            new MogakkoCreateRequestDto(dummyUsers.get(0).getId(), "title",
                LocationInfoDto.create(mogakkoLocation),
                LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(1),
                10, "", List.of()));
        Mogakko mogakko = mogakkoService.getByIdNotDeleted(responseDto.id());

        // when
        chatRoomService.enterChatRoom(new ChatEnterRequestDto(mogakko.getChatRoom().getId(), dummyUsers.get(1)));
        chatRoomService.enterChatRoom(new ChatEnterRequestDto(mogakko.getChatRoom().getId(), dummyUsers.get(2)));

        // then
        TransactionStatus status = tx.getTransaction(new DefaultTransactionDefinition());

        ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedAtIsNull(
            mogakko.getChatRoom().getId()).orElseThrow(() -> new ChatException(ChatErrorType.CHATROOM_NOT_FOUND));

        assertThat(chatRoom.getMogakko().getId()).isEqualTo(mogakko.getId());
        assertThat(chatRoom.getCreator().getId()).isEqualTo(dummyUsers.get(0).getId());
        assertThat(chatRoom.getChatParticipants()).hasSize(3);

        tx.rollback(status);
    }
}
