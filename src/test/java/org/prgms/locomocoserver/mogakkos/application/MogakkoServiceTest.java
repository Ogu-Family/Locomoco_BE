package org.prgms.locomocoserver.mogakkos.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.prgms.locomocoserver.categories.domain.Category;
import org.prgms.locomocoserver.categories.domain.CategoryRepository;
import org.prgms.locomocoserver.categories.domain.CategoryType;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTag;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTagRepository;
import org.prgms.locomocoserver.mogakkos.domain.participants.Participant;
import org.prgms.locomocoserver.mogakkos.domain.participants.ParticipantRepository;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoUpdateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoDetailResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoUpdateResponseDto;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.tags.domain.TagRepository;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.domain.enums.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class MogakkoServiceTest {

    private final List<Long> tagIds = new ArrayList<>();
    @Autowired
    private MogakkoService mogakkoService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private MogakkoTagRepository mogakkoTagRepository;
    @Autowired
    private MogakkoRepository mogakkoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    private User setUpUser1, setUpUser2;
    private Mogakko testMogakko;

    @BeforeAll
    void setUp() {
        Category langs = Category.builder().categoryType(CategoryType.MOGAKKO).name("개발 언어")
            .build();
        Category coding = Category.builder().categoryType(CategoryType.MOGAKKO).name("개발 유형")
            .build();

        categoryRepository.saveAll(List.of(langs, coding));

        Tag js = Tag.builder().name("JS").build();
        Tag python = Tag.builder().name("python").build();
        langs.addTag(js);
        langs.addTag(python);

        Tag codingTest = Tag.builder().name("코테").build();
        Tag backend = Tag.builder().name("백엔드").build();
        coding.addTag(codingTest);
        coding.addTag(backend);

        tagRepository.saveAll(List.of(js, python, codingTest, backend));

        setUpUser1 = userRepository.save(
            User.builder().nickname("생성자").email("cho@gmail.com").job(Job.JOB_SEEKER).birth(
                LocalDate.EPOCH).gender(Gender.MALE).temperature(36.5).provider("github").build());
        setUpUser2 = userRepository.save(
            User.builder().nickname("참여자1").email("part@gmail.com").job(Job.DEVELOPER).birth(
                LocalDate.EPOCH).gender(Gender.FEMALE).temperature(42.1).provider("kakao").build());
        testMogakko = Mogakko.builder().title("title").content("제곧내").views(20).likeCount(10)
            .location("어딘가").startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(2)).deadline(LocalDateTime.now().plusHours(1))
            .creator(setUpUser1).build();

        mogakkoRepository.save(testMogakko);
        tagRepository.findAll().forEach(tag -> mogakkoTagRepository.save(MogakkoTag.builder()
            .tag(tag).mogakko(testMogakko).build()));
        participantRepository.save(
            Participant.builder().user(setUpUser2).mogakko(testMogakko).build());

        tagIds.addAll(List.of(js.getId(), python.getId(), codingTest.getId(), backend.getId()));
    }

    @AfterAll
    void tearDown() {
        participantRepository.deleteAll();
        mogakkoTagRepository.deleteAll();
        mogakkoRepository.deleteAll();
        userRepository.deleteAll();
        tagRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("모각코 생성이 제대로 되는지 확인한다")
    void success_create_mogakko() {
        // given
        User savedCreator = userRepository.save(
            User.builder().nickname("생성자").email("cho@gmail.com").job(Job.JOB_SEEKER).birth(
                LocalDate.EPOCH).gender(Gender.MALE).temperature(36.5).provider("github").build());

        LocalDateTime startTime = LocalDateTime.now();
        MogakkoCreateRequestDto mogakkoCreateRequestDto = new MogakkoCreateRequestDto(
            savedCreator.getId(),
            "제목",
            "장소",
            startTime,
            startTime.plusHours(2),
            startTime.plusHours(1),
            null,
            "내용",
            List.of(tagIds.get(0), tagIds.get(1), tagIds.get(2)));

        // when
        Long savedMogakkoId = mogakkoService.save(mogakkoCreateRequestDto);

        // then
        Optional<Mogakko> mogakkoOptional = mogakkoRepository.findByIdAndDeletedAtIsNull(
            savedMogakkoId);
        assertThat(mogakkoOptional.isPresent()).isTrue();

        Mogakko createdMogakko = mogakkoOptional.get();
        assertThat(createdMogakko.getTitle()).isEqualTo("제목");
        assertThat(createdMogakko.getMaxParticipants()).isEqualTo(Mogakko.DEFAULT_MAX_PARTICIPANTS);
        assertThat(createdMogakko.getViews()).isEqualTo(0);
        assertThat(createdMogakko.getCreator().getId()).isEqualTo(savedCreator.getId());

        assertThat(mogakkoTagRepository.findAllByMogakko(createdMogakko)).hasSize(3);
    }

    @Test
    @DisplayName("id 값으로 특정 모각코 디테일 정보를 가져올 수 있다")
    @Order(1)
    void success_find_mogakko_info_in_detail() {
        // when
        MogakkoDetailResponseDto responseDto = mogakkoService.findDetail(testMogakko.getId());

        // then
        assertThat(responseDto.creatorInfo()).isNotNull();
        assertThat(responseDto.creatorInfo().nickname()).isEqualTo("생성자");
        assertThat(responseDto.participants()).hasSize(1);
        assertThat(responseDto.MogakkoInfo().title()).isEqualTo("title");
        assertThat(responseDto.MogakkoInfo().tagIds()).hasSize(4);
    }

    @Test
    @DisplayName("모각코 정보를 업데이트 할 수 있다")
    @Order(2)
    void success_update_mogakko_info_and_tags() {
        // given
        String updateTitle = "바뀐 제목";
        String updateLocation = "바뀐 장소";
        String updateContent = "바뀐 내용";

        MogakkoUpdateRequestDto requestDto = new MogakkoUpdateRequestDto(
            setUpUser1.getId(), updateTitle, updateLocation,
            testMogakko.getStartTime(), testMogakko.getStartTime().plusHours(5),
            testMogakko.getDeadline(),
            testMogakko.getMaxParticipants(), updateContent, List.of(tagIds.get(2)));

        // when
        MogakkoUpdateResponseDto responseDto = mogakkoService.update(requestDto, testMogakko.getId());

        // then
        assertThat(responseDto.id()).isEqualTo(testMogakko.getId());

        Optional<Mogakko> mogakkoOptional = mogakkoRepository.findById(testMogakko.getId());
        assertThat(mogakkoOptional.isPresent()).isTrue();

        Mogakko updatedMogakko = mogakkoOptional.get();
        List<MogakkoTag> updatedMogakkoTags = mogakkoTagRepository.findAllByMogakko(updatedMogakko);

        assertThat(updatedMogakko.getTitle()).isEqualTo(updateTitle);
        assertThat(updatedMogakko.getContent()).isEqualTo(updateContent);
        assertThat(updatedMogakko.getLocation()).isEqualTo(updateLocation);
        assertThat(updatedMogakko.getEndTime().truncatedTo(ChronoUnit.MILLIS))
            .isEqualTo(testMogakko.getStartTime().plusHours(5).truncatedTo(ChronoUnit.MILLIS));

        assertThat(updatedMogakkoTags).hasSize(1);
        assertThat(updatedMogakkoTags.get(0).getTag().getId()).isEqualTo(tagIds.get(2));
    }
}
