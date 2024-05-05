package org.prgms.locomocoserver.chat.application;

import org.junit.jupiter.api.*;
import org.prgms.locomocoserver.categories.domain.CategoryRepository;
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

    @BeforeAll
    void setUp() {
        mongoTemplate.getDb().drop();
        userRepository.deleteAll();
        imageRepository.deleteAll();
        tagRepository.deleteAll();
        categoryRepository.deleteAll();

        mongoTemplate.createCollection("chat_messages_1");
    }

    @Test
    @Order(1)
    @DisplayName("채팅방 입장 메시지를 저장할 수 있다.")
    @Transactional
    void saveEnterMessage() {
        // given
        Long roomId = 1L;
        User sender = testFactory.createUser();
        categoryRepository.save(sender.getJobTag().getCategory());
        imageRepository.save(sender.getProfileImage());
        tagRepository.save(sender.getJobTag());
        sender = userRepository.save(sender);

        // when
        mongoChatMessageService.saveEnterMessage(roomId, sender);
        boolean collectionExists = mongoTemplate.collectionExists("chat_messages_1");

        // then
        assertThat(collectionExists).isTrue();
    }

    @Test
    @Order(2)
    @DisplayName("채팅 메시지를 저장할 수 있다.")
    @Transactional
    void saveChatMessage() {
        // given
        Long roomId = 1L;
        User sender = testFactory.createUser();

        categoryRepository.save(sender.getJobTag().getCategory());
        imageRepository.save(sender.getProfileImage());
        tagRepository.save(sender.getJobTag());
        sender = userRepository.save(sender);
        Mogakko mogakko = mogakkoRepository.save(testFactory.createMogakko(sender));
        chatRoomRepository.save(testFactory.createChatRoom(sender, mogakko));

        Long senderId = sender.getId();

        // when
        mongoChatMessageService.saveChatMessage(roomId, new ChatMessageRequestDto(roomId, senderId, "message"));
        boolean collectionExists = mongoTemplate.collectionExists("chat_messages_1");
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
        Long roomId = 1L;
        String collectionName = mongoChatMessageService.getChatRoomName(roomId);

        // when
        List<ChatMessageDto> chatMessageMongoList = mongoChatMessageService.getAllChatMessages(roomId, "null", 10);

        // then
        assertThat(chatMessageMongoList.size()).isEqualTo(2);
    }
}