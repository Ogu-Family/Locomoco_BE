package org.prgms.locomocoserver.chat.domain;

import org.junit.jupiter.api.*;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongo;
import org.prgms.locomocoserver.chat.domain.mongo.ChatRoomMongo;
import org.prgms.locomocoserver.chat.domain.mongo.ChatRoomMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChatRoomMongoRepositoryTest {

    @Autowired
    private ChatRoomMongoRepository chatRoomMongoRepository;

    private ChatRoomMongo chatRoom;

    @BeforeAll
    void setUp() {
        chatRoomMongoRepository.deleteAll();
        this.chatRoom = ChatRoomMongo.builder()
                .messages(new ArrayList<>()).build();
    }

    @Test
    @Order(1)
    void insertChatRoomMongo() {
        // given

        // when
        chatRoomMongoRepository.insert(chatRoom);

        // then
        assertThat(chatRoomMongoRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @Order(2)
    void insertChatMessageMongo() {
        // given
        ChatMessageMongo chatMessage = ChatMessageMongo.builder()
                .message("chat message")
                .senderId("senderId")
                .createdAt(LocalDateTime.now()).build();

        // when
        chatRoom.addChatMessage(chatMessage);
        chatRoomMongoRepository.save(chatRoom);

        // then
        assertThat(chatRoom.getMessages().size()).isEqualTo(1);
    }

}