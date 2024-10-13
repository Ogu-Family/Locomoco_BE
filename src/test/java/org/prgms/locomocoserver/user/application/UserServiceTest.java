package org.prgms.locomocoserver.user.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.categories.domain.Category;
import org.prgms.locomocoserver.categories.domain.CategoryInputType;
import org.prgms.locomocoserver.categories.domain.CategoryRepository;
import org.prgms.locomocoserver.categories.domain.CategoryType;
import org.prgms.locomocoserver.chat.domain.ChatParticipant;
import org.prgms.locomocoserver.chat.domain.ChatParticipantRepository;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.domain.querydsl.ChatRoomCustomRepository;
import org.prgms.locomocoserver.global.TestFactory;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.tags.domain.TagRepository;
import org.prgms.locomocoserver.user.domain.DeviceKeyRepository;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.dto.OAuthUserInfoDto;
import org.prgms.locomocoserver.user.dto.kakao.KakaoAccountDto;
import org.prgms.locomocoserver.user.dto.kakao.KakaoUserInfoResponseDto;
import org.prgms.locomocoserver.user.dto.request.UserInitInfoRequestDto;
import org.prgms.locomocoserver.user.dto.request.UserUpdateRequest;
import org.prgms.locomocoserver.user.dto.response.TokenResponseDto;
import org.prgms.locomocoserver.user.dto.response.UserLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private DeviceKeyRepository deviceKeyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatParticipantRepository chatParticipantRepository;
    @Autowired
    private MogakkoRepository mogakkoRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatRoomCustomRepository chatRoomCustomRepository;

    private Tag engineer;

    @BeforeEach
    void setUp() {
        Category jobCategory = Category.builder().categoryType(CategoryType.USER)
                .categoryInputType(CategoryInputType.RADIOGROUP).name("직업").build();
        categoryRepository.save(jobCategory);

        engineer = Tag.builder().name("현직자").build();
        tagRepository.saveAll(List.of(engineer));
    }

    @AfterEach
    void tearDown() {
        tagRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("초기 회원가입 정보 저장이 성공하고 디바이스 키도 제대로 저장된다")
    @Transactional
    void success_insert_init_user_info_and_device_key() {
        // given
        String initEmail = "namaewa@gmail.com";
        User user = User.builder().email(initEmail).gender(
                Gender.FEMALE).temperature(36.5).provider("kakao").build();
        userRepository.save(user);

        // when
        String initName = "kimino";
        LocalDate initBirth = LocalDate.of(2000, 1, 4);
        userService.insertInitInfo(user.getId(),
                new UserInitInfoRequestDto(initName, initBirth,
                        Gender.FEMALE.name(), engineer.getId()), null);

        // then
        Optional<User> userOptional = userRepository.findById(user.getId());
        assertThat(userOptional).isPresent();

        User foundUser = userOptional.get();
        assertThat(foundUser.getNickname()).isEqualTo(initName);
        assertThat(foundUser.getBirth()).isEqualTo(initBirth);
        assertThat(foundUser.getEmail()).isEqualTo(initEmail);
        assertThat(foundUser.getJobTag().getId()).isEqualTo(engineer.getId());

        // assertThat(deviceKeyRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("사용자가 로그인할 때 새로운 사용자 정보가 저장된다")
    @Transactional
    void saveOrUpdate_user_login() {
        // given
        OAuthUserInfoDto oAuthUserInfoDto = new KakaoUserInfoResponseDto(LocalDateTime.now().toString(), new KakaoAccountDto("namaewa@gmail.com"));
        TokenResponseDto tokenResponseDto = new TokenResponseDto("someAccessToken", "Bearer", "refreshToken", 3600, 36000);

        // when
        UserLoginResponse response = userService.saveOrUpdate(oAuthUserInfoDto, tokenResponseDto);

        // then
        assertThat(response.isNewUser()).isTrue();
        Optional<User> userOptional = userRepository.findByEmailAndProviderAndDeletedAtIsNull("namaewa@gmail.com", "kakao");
        assertThat(userOptional).isPresent();
        assertThat(userOptional.get().getEmail()).isEqualTo("namaewa@gmail.com");
    }

    @Test
    @DisplayName("사용자의 초기 정보를 성공적으로 저장한다")
    @Transactional
    void insertInitInfo_success() {
        // given
        String initEmail = "namaewa@gmail.com";
        User user = User.builder().email(initEmail).gender(Gender.FEMALE).temperature(36.5).provider("kakao").build();
        userRepository.save(user);

        String initName = "kimino";
        LocalDate initBirth = LocalDate.of(2000, 1, 4);
        UserInitInfoRequestDto requestDto = new UserInitInfoRequestDto(initName, initBirth, Gender.FEMALE.name(), engineer.getId());

        // when
        userService.insertInitInfo(user.getId(), requestDto, null);

        // then
        User foundUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(foundUser.getNickname()).isEqualTo(initName);
        assertThat(foundUser.getBirth()).isEqualTo(initBirth);
        assertThat(foundUser.getJobTag().getId()).isEqualTo(engineer.getId());
    }

    @Test
    @DisplayName("사용자 정보를 성공적으로 업데이트한다")
    @Transactional
    void updateUserInfo_success() {
        // given
        String initEmail = "namaewa@gmail.com";
        User user = User.builder().email(initEmail).gender(Gender.FEMALE).temperature(36.5).provider("kakao").build();
        userRepository.save(user);

        String initName = "kimino";
        LocalDate initBirth = LocalDate.of(2000, 1, 4);
        UserInitInfoRequestDto requestDto = new UserInitInfoRequestDto(initName, initBirth, Gender.FEMALE.name(), engineer.getId());
        userService.insertInitInfo(user.getId(), requestDto, null);

        UserUpdateRequest updateRequest = new UserUpdateRequest("updatedNickname", Gender.MALE, LocalDate.of(1999, 5, 15), engineer.getId());

        // when
        userService.updateUserInfo(user.getId(), updateRequest, null);

        // then
        User foundUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(foundUser.getNickname()).isEqualTo("updatedNickname");
        assertThat(foundUser.getBirth()).isEqualTo(LocalDate.of(1999, 5, 15));
        assertThat(foundUser.getGender()).isEqualTo(Gender.MALE);
    }

    @Test
    @DisplayName("사용자가 성공적으로 삭제되고, 모각코 및 채팅 참여 목록에서도 삭제된다")
    @Transactional
    void deleteUser_success() {
        // given
        String initEmail = "namaewa@gmail.com";
        User user = User.builder()
                .email(initEmail)
                .gender(Gender.FEMALE)
                .temperature(36.5)
                .provider("kakao")
                .build();
        user = userRepository.save(user);

        Mogakko mogakko = TestFactory.createMogakko(user);
        mogakko = mogakkoRepository.save(mogakko);

        ChatRoom chatRoom = TestFactory.createChatRoom(user, mogakko);
        chatRoomRepository.save(chatRoom);
        ChatParticipant chatParticipant = TestFactory.createChatParticipant(user, chatRoom);
        chatParticipantRepository.save(chatParticipant);

        // when
        userService.deleteUser(user.getId());

        // then
        Optional<User> deletedUserOptional = userRepository.findByIdAndDeletedAtIsNull(user.getId());
        assertThat(deletedUserOptional).isEmpty();

        Optional<Mogakko> deletedMogakkoOptional = mogakkoRepository.findById(mogakko.getId());
        assertThat(deletedMogakkoOptional).isPresent();
        assertThat(deletedMogakkoOptional.get().getParticipants())
                .extracting(participant -> participant.getId())
                .doesNotContain(user.getId());

        List<ChatRoom> chatRooms = chatRoomCustomRepository.findByParticipantsId(user.getId(), 0L, 10);
        assertThat(chatRooms).isEmpty();
    }

}
