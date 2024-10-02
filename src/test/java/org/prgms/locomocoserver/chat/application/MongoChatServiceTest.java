package org.prgms.locomocoserver.chat.application;

import org.junit.jupiter.api.*;
import org.prgms.locomocoserver.categories.domain.CategoryRepository;
import org.prgms.locomocoserver.chat.domain.ChatParticipant;
import org.prgms.locomocoserver.chat.domain.ChatParticipantRepository;
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
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
    ImageService imageService;

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
    ChatParticipantRepository chatParticipantRepository;

    private User creator;
    private ChatRoom chatRoom;

    @BeforeAll
    void setUp() {
        mongoTemplate.getDb().drop();
        chatParticipantRepository.deleteAll();
        chatRoomRepository.deleteAll();
        mogakkoRepository.deleteAll();
        userRepository.deleteAll();
        imageRepository.deleteAll();
        categoryRepository.deleteAll();

        User sender = TestFactory.createUser();
        imageRepository.save(sender.getProfileImage());
        creator = userRepository.save(sender);

        Mogakko mogakko = mogakkoRepository.save(TestFactory.createMogakko(creator));
        chatRoom = chatRoomRepository.save(TestFactory.createChatRoom(creator, mogakko));

        mongoChatMessageService.saveEnterMessage(sender.getId(), sender);
        chatParticipantRepository.save(TestFactory.createChatParticipant(sender, chatRoom));
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
        boolean collectionExists = mongoTemplate.collectionExists("chat_messages_" + roomId);

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
        String collectionName = mongoChatMessageService.getChatRoomName(roomId);
        long beforeMessageCount = mongoTemplate.getCollection(collectionName).countDocuments();

        byte[] byteCode = imageToByteArray("src/test/resources/스누피4.jpeg");
        String imageBase64 = Base64.getEncoder().encodeToString(byteCode);
        ChatMessageRequestDto requestDto = new ChatMessageRequestDto(roomId, senderId, "message", List.of(imageBase64));

        // when
        List<String> imageUrls = chatImageService.create(requestDto);
        mongoChatMessageService.saveChatMessageWithImage(roomId, imageUrls, requestDto);

        boolean collectionExists = mongoTemplate.collectionExists(collectionName);
        long messageCount = mongoTemplate.getCollection(collectionName).countDocuments();

        List<ChatMessageMongo> messages = mongoTemplate.findAll(ChatMessageMongo.class, collectionName);
        assertThat(messages).isNotEmpty();
        ChatMessageMongo lastMessage = messages.get(messages.size() - 1);

        // then
        assertThat(collectionExists).isTrue();
        assertThat(messageCount).isEqualTo(beforeMessageCount + 1);
        assertThat(lastMessage.getImageUrls().size()).isEqualTo(1);
        assertThat(lastMessage.getImageUrls().get(0).contains(".jpeg")).isTrue();
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

    private byte[] imageToByteArray(String imagePath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 이미지 파일을 읽어 BufferedImage로 변환
            BufferedImage image = ImageIO.read(new File(imagePath));

            // BufferedImage를 바이트 배열로 변환
            ImageIO.write(image, "jpeg", baos);
            baos.flush();

            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
