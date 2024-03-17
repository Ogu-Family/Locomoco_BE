package org.prgms.locomocoserver.user.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.categories.domain.Category;
import org.prgms.locomocoserver.categories.domain.CategoryInputType;
import org.prgms.locomocoserver.categories.domain.CategoryRepository;
import org.prgms.locomocoserver.categories.domain.CategoryType;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.tags.domain.TagRepository;
import org.prgms.locomocoserver.user.domain.DeviceKeyRepository;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.dto.request.UserInitInfoRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        LocalDate initBirth = LocalDate.of(2017, 1, 4);
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

        assertThat(deviceKeyRepository.findAll()).hasSize(1);
    }
}
