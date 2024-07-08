package org.prgms.locomocoserver.inquiries.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.prgms.locomocoserver.global.TestFactory.createMogakko;
import static org.prgms.locomocoserver.global.TestFactory.createUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.image.domain.ImageRepository;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class InquiryRepositoryTest {
    private static final long CURSOR = Long.MAX_VALUE;
    private static final int PAGE_SIZE = 20;
    private static final int CREATE_INQUIRY_COUNT = 20;
    private static final int CREATE_USER_COUNT = 3;

    @Autowired
    private InquiryRepository inquiryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MogakkoRepository mogakkoRepository;
    @Autowired
    private ImageRepository imageRepository;

    private final List<User> users = new ArrayList<>();
    private Mogakko mogakko;
    private int[] notDeletedInquiryCnt, deletedInquiryCnt;

    @BeforeEach
    void setUp() {
        createUsers();
        imageRepository.saveAll(users.stream().map(User::getProfileImage).toList());

        mogakko = createMogakko(users.get(0));

        notDeletedInquiryCnt = new int[users.size()];
        deletedInquiryCnt = new int[users.size()];

        List<Inquiry> inquiries = createRandomInquiry();

        userRepository.saveAll(users);
        mogakkoRepository.save(mogakko);
        inquiryRepository.saveAll(inquiries);
    }

    @AfterEach
    void tearDown() {
        imageRepository.deleteAll();
        inquiryRepository.deleteAll();
        mogakkoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("DB에 저장된 모든 문의를 특정 사이즈만큼 가져올 수 있다.")
    void success_find_all_given_normal_condition() {
        // when
        List<Inquiry> inquiries = inquiryRepository.findAll(CURSOR, PAGE_SIZE);

        // then
        assertThat(inquiries).hasSize(Math.min(PAGE_SIZE * CREATE_USER_COUNT, CREATE_INQUIRY_COUNT));
    }

    @Test
    @DisplayName("각 유저에 대해 모든 문의를 특정 사이즈만큼 가져올 수 있다.")
    void success_find_all_by_user() {
        // when then
       IntStream.range(0, CREATE_USER_COUNT).forEach(idx -> {
                User u = users.get(idx);
                int inquirySum = deletedInquiryCnt[idx] + notDeletedInquiryCnt[idx];

                assertThat(inquiryRepository.findAllByUser(CURSOR, u.getId(), PAGE_SIZE))
                    .hasSize(Math.min(PAGE_SIZE, inquirySum));
            });
    }

    @Test
    @DisplayName("모각코와 연관된 삭제되지 않은 모든 문의를 특정 사이즈만큼 가져올 수 있다.")
    void success_find_all_except_deleted_by_mogakko() {
        // given
        int existInquiriesCount = Arrays.stream(notDeletedInquiryCnt).sum();

        // when
        List<Inquiry> inquiries = inquiryRepository.findAllByMogakko(CURSOR, mogakko.getId(),
            PAGE_SIZE);

        // then
        assertThat(inquiries).hasSize(Math.min(PAGE_SIZE, existInquiriesCount));
    }

    @Test
    @DisplayName("특정 모각코와 특정 유저에 연관된 모든 문의를 특정 사이즈만큼 가져올 수 있다.")
    void success_find_all_by_mogakko_and_user() {
        // when then
        IntStream.range(0, CREATE_USER_COUNT).forEach(idx -> {
            User u = users.get(idx);
            int inquirySum = deletedInquiryCnt[idx] + notDeletedInquiryCnt[idx];

            assertThat(inquiryRepository.findAllByUser(CURSOR, u.getId(), PAGE_SIZE))
                .hasSize(Math.min(PAGE_SIZE, inquirySum));
        });
    }

    void createUsers() {
        IntStream.range(0, CREATE_USER_COUNT).forEach(i -> users.add(createUser()));
    }

    List<Inquiry> createRandomInquiry() {
        List<Inquiry> inquiries = new ArrayList<>();
        Random random = new Random();

        IntStream.range(0, CREATE_INQUIRY_COUNT).forEach(i -> {
            int userId = random.nextInt(CREATE_USER_COUNT);
            int isDeleted = random.nextInt(2);

            Inquiry inquiry = Inquiry.builder().user(users.get(userId)).mogakko(mogakko).content("")
                .build();

            inquiries.add(inquiry);

            if (isDeleted == 0) {
                inquiry.delete();
                deletedInquiryCnt[userId]++;
            } else {
                notDeletedInquiryCnt[userId]++;
            }

        });

        return inquiries;
    }
}
