package org.prgms.locomocoserver.chat.application;

import org.junit.jupiter.api.*;
import org.prgms.locomocoserver.categories.domain.CategoryRepository;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongo;
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
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MongoChatServiceTest {

    @Autowired
    MongoChatService mongoChatService;
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
    }

    @Test
    @Order(1)
    @DisplayName("채팅방을 만들 수 있다.")
    void createChatRoom() {
        // given
        Long roomId = 1L;

        // when
        mongoChatService.createChatRoom(roomId);
        boolean collectionExists = mongoTemplate.collectionExists("chat_messages_1");

        // then
        assertThat(collectionExists).isTrue();
    }

    @Test
    @Order(2)
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
        Long senderId = sender.getId();

        // when
        mongoChatService.saveEnterMessage(roomId, senderId);
        boolean collectionExists = mongoTemplate.collectionExists("chat_messages_1");

        // then
        assertThat(collectionExists).isTrue();
    }

    @Test
    @Order(3)
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
        mongoChatService.saveChatMessage(roomId, new ChatMessageRequestDto(roomId, senderId, "message"));
        boolean collectionExists = mongoTemplate.collectionExists("chat_messages_1");
        String collectionName = mongoChatService.getChatRoomName(roomId);
        long messageCount = mongoTemplate.getCollection(collectionName).countDocuments();

        // then
        assertThat(collectionExists).isTrue();
        assertThat(messageCount).isEqualTo(2);
    }

    @Test
    @DisplayName("채팅방의 메시지를 모두 불러올 수 있다.")
    void getAllChatMessages() {
        // given
        Long roomId = 1L;
        String collectionName = mongoChatService.getChatRoomName(roomId);

        // when
        List<ChatMessageDto> chatMessageMongoList = mongoChatService.getAllChatMessages(roomId);

        // then
        assertThat(chatMessageMongoList.size()).isEqualTo(2);
    }
}