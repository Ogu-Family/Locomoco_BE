package org.prgms.locomocoserver.chat.application;

import org.junit.jupiter.api.*;
import org.prgms.locomocoserver.categories.domain.CategoryRepository;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongo;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.global.TestFactory;
import org.prgms.locomocoserver.image.application.ImageService;
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

import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MongoChatServiceTest {

    @Autowired
    MongoChatMessageService mongoChatMessageService;
    @Autowired
    ChatImageService chatImageService;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;
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
        chatRoomRepository.deleteAll();
        mogakkoRepository.deleteAll();
        userRepository.deleteAll();
        imageRepository.deleteAll();
        categoryRepository.deleteAll();

        User sender = testFactory.createUser();
        imageRepository.save(sender.getProfileImage());
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

        byte[] byteCode = new byte[]{
                (byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0A, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0A,
                (byte)0x08, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x50, (byte)0x58,
                (byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x15, (byte)0x49, (byte)0x44, (byte)0x41,
                (byte)0x54, (byte)0x78, (byte)0x9C, (byte)0x63, (byte)0xFC, (byte)0xFF, (byte)0xFF, (byte)0x3F,
                (byte)0x03, (byte)0x6E, (byte)0xC0, (byte)0x84, (byte)0x47, (byte)0x6E, (byte)0x04, (byte)0x4B,
                (byte)0x03, (byte)0x00, (byte)0xA5, (byte)0xE3, (byte)0x03, (byte)0x11, (byte)0x7D, (byte)0x92,
                (byte)0xA6, (byte)0x6A, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45,
                (byte)0x4E, (byte)0x44, (byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
        };
        String imageBase64 = Base64.getEncoder().encodeToString(byteCode);
        ChatMessageRequestDto requestDto = new ChatMessageRequestDto(roomId, senderId, "message", List.of(imageBase64));

        // when
        List<String> imageUrls = chatImageService.create(requestDto);
        mongoChatMessageService.saveChatMessageWithImage(roomId, imageUrls, requestDto);

        String collectionName = mongoChatMessageService.getChatRoomName(roomId);
        boolean collectionExists = mongoTemplate.collectionExists(collectionName);
        long messageCount = mongoTemplate.getCollection(collectionName).countDocuments();

        List<ChatMessageMongo> messages = mongoTemplate.findAll(ChatMessageMongo.class, collectionName);
        assertThat(messages).isNotEmpty();
        ChatMessageMongo lastMessage = messages.get(messages.size() - 1);

        // then
        assertThat(collectionExists).isTrue();
        assertThat(messageCount).isEqualTo(2);
        assertThat(lastMessage.getImageUrls().size()).isEqualTo(1);
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
