package org.prgms.locomocoserver.chat.repository;

import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.*;
import org.prgms.locomocoserver.chat.domain.ChatParticipant;
import org.prgms.locomocoserver.chat.domain.ChatParticipantRepository;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.prgms.locomocoserver.chat.domain.querydsl.ChatRoomCustomRepository;
import org.prgms.locomocoserver.global.TestFactory;
import org.prgms.locomocoserver.image.domain.ImageRepository;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.Assert.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatRoomCustomRepository chatRoomCustomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MogakkoRepository mogakkoRepository;
    @Autowired
    private ChatParticipantRepository chatParticipantRepository;
    @Autowired
    private ImageRepository imageRepository;

    private User user1;
    private ChatRoom chatRoom1;

    @BeforeEach
    void setUp() {
        User user = TestFactory.createUser();
        imageRepository.save(user.getProfileImage());
        user1 = userRepository.save(user);

        Mogakko mogakko = TestFactory.createMogakko(user);
        mogakkoRepository.save(mogakko);

        ChatRoom chatRoom = TestFactory.createChatRoom(user, mogakko);
        chatRoom1 = chatRoomRepository.save(chatRoom);

        ChatParticipant participant = TestFactory.createChatParticipant(user, chatRoom);
        chatRoom1.addChatParticipant(chatParticipantRepository.save(participant));
    }

    @AfterAll
    void tearDown() {
        chatParticipantRepository.deleteAll();
        chatRoomRepository.deleteAll();
        mogakkoRepository.deleteAll();
        userRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    @DisplayName("JPA Repository 활용")
    void findByParticipantsIdWithJPA() {
        // given
        User user2 = TestFactory.createUser();
        imageRepository.save(user2.getProfileImage());
        userRepository.save(user2);

        ChatParticipant chatParticipant = TestFactory.createChatParticipant(user2, chatRoom1);
        chatParticipant = chatParticipantRepository.save(chatParticipant);

        chatRoom1.addChatParticipant(chatParticipant);

        // when
        List<ChatRoom> chatRooms = chatRoomRepository.findByParticipantsId(user1.getId(), Long.MAX_VALUE, 10);
        // fetch join X, Transaction X, Lazy Loading
        assertThrows(LazyInitializationException.class, () -> {
            ChatRoomDto.of(chatRooms.get(0), 0, null);
        });

        // then
        System.out.println("Participants Num : " + chatRooms.size());
        System.out.println("User : " + chatRooms.get(0).getMogakko().getId());
    }

    @Test
    @DisplayName("QueryDSL 활용")
    void findByParticipantsWithQueryDSL() {
        // given

        // when
        List<ChatRoom> chatRooms = chatRoomCustomRepository.findByParticipantsId(user1.getId(), Long.MAX_VALUE, 10);
        ChatRoomDto.of(chatRooms.get(0), 0,null);

        // then
        System.out.println("Participants Num : " + chatRooms.size());
        System.out.println("User : " + chatRooms.get(0).getMogakko().getId());
    }
}
