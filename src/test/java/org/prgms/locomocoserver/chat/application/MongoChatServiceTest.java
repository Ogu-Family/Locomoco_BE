package org.prgms.locomocoserver.chat.application;

import org.junit.jupiter.api.*;
import org.prgms.locomocoserver.categories.domain.CategoryRepository;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.global.TestFactory;
import org.prgms.locomocoserver.image.domain.ImageRepository;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.tags.domain.TagRepository;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MongoChatServiceTest {

    @Autowired
    MongoChatMessageService mongoChatMessageService;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    MogakkoRepository mogakkoRepository;
    @Autowired
    TestFactory testFactory;

    private User creator;
    private ChatRoom chatRoom;

    @BeforeAll
    void setUp() {
        mongoTemplate.getDb().drop();
        userRepository.deleteAll();
        imageRepository.deleteAll();
        tagRepository.deleteAll();
        categoryRepository.deleteAll();
        chatRoomRepository.deleteAll();

        User sender = testFactory.createUser();
        categoryRepository.save(sender.getJobTag().getCategory());
        imageRepository.save(sender.getProfileImage());
        tagRepository.save(sender.getJobTag());
        creator = userRepository.save(sender);

        Mogakko mogakko = mogakkoRepository.save(testFactory.createMogakko(creator));
        chatRoom = chatRoomRepository.save(testFactory.createChatRoom(creator, mogakko));
    }

    @Test
    @Order(1)
    @DisplayName("채팅방 입장 메시지를 저장할 수 있다.")
    @Transactional
    void saveEnterMessage() {
        // given
        Long roomId = chatRoom.getId();

        // when
        mongoChatMessageService.saveEnterMessage(roomId, creator);
        boolean collectionExists = mongoTemplate.collectionExists("chat_messages_"+roomId);

        // then
        assertThat(collectionExists).isTrue();
    }

    @Test
    @Order(2)
    @DisplayName("채팅 메시지를 저장할 수 있다.")
    @Transactional
    void saveChatMessage() {
        // given
        Long roomId = chatRoom.getId();
        Long senderId = creator.getId();

        // when
        mongoChatMessageService.saveChatMessage(roomId, new ChatMessageRequestDto(roomId, senderId, "message"));
        boolean collectionExists = mongoTemplate.collectionExists("chat_messages_" + roomId);
        String collectionName = mongoChatMessageService.getChatRoomName(roomId);
        long messageCount = mongoTemplate.getCollection(collectionName).countDocuments();

        // then
        assertThat(collectionExists).isTrue();
        assertThat(messageCount).isEqualTo(2);
    }

    @Test
    @Order(3)
    @DisplayName("채팅방의 메시지를 모두 불러올 수 있다.")
    void getAllChatMessages() {
        // given
        Long roomId = chatRoom.getId();
        String collectionName = mongoChatMessageService.getChatRoomName(roomId);

        // when
        List<ChatMessageDto> chatMessageMongoList = mongoChatMessageService.getAllChatMessages(roomId, "null", 10);
        assertThat(chatMessageMongoList.size()).isEqualTo(2);

        String cursor = chatMessageMongoList.get(1).chatMessageId();
        List<ChatMessageDto> chatMessageMongoList2 = mongoChatMessageService.getAllChatMessages(roomId, cursor, 10);

        // then
        assertThat(chatMessageMongoList2.size()).isEqualTo(1);
    }
}