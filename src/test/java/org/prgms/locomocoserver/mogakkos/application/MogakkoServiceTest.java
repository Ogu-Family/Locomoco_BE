package org.prgms.locomocoserver.mogakkos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.qos.logback.core.util.ExecutorServiceUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.prgms.locomocoserver.categories.domain.Category;
import org.prgms.locomocoserver.categories.domain.CategoryInputType;
import org.prgms.locomocoserver.categories.domain.CategoryRepository;
import org.prgms.locomocoserver.categories.domain.CategoryType;
import org.prgms.locomocoserver.chat.domain.ChatMessageRepository;
import org.prgms.locomocoserver.chat.domain.ChatParticipantRepository;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocation;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocationRepository;
import org.prgms.locomocoserver.mogakkos.dto.LocationInfoDto;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTag;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTagRepository;
import org.prgms.locomocoserver.mogakkos.domain.participants.Participant;
import org.prgms.locomocoserver.mogakkos.domain.participants.ParticipantRepository;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoUpdateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoCreateResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoDetailResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoSimpleInfoResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoUpdateResponseDto;
import org.prgms.locomocoserver.mogakkos.exception.MogakkoErrorType;
import org.prgms.locomocoserver.mogakkos.exception.MogakkoException;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.tags.domain.TagRepository;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.exception.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class MogakkoServiceTest {
    static final long CURSOR = Long.MAX_VALUE;
    static final int PAGE_SIZE = 10;

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
    @Autowired
    private MogakkoLocationRepository mogakkoLocationRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatParticipantRepository chatParticipantRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    private User setUpUser1, setUpUser2;
    private Mogakko testMogakko;
    private final List<Long> tagIds = new ArrayList<>();

    @BeforeAll
    void setUp() {
        Category langs = Category.builder().categoryType(CategoryType.MOGAKKO).name("개발 언어")
            .categoryInputType(CategoryInputType.CHECKBOX).build();
        Category coding = Category.builder().categoryType(CategoryType.MOGAKKO).name("개발 유형")
            .categoryInputType(CategoryInputType.COMBOBOX).build();

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
            User.builder().nickname("생성자").email("cho@gmail.com").birth(
                LocalDate.EPOCH).gender(Gender.MALE).temperature(36.5).provider("github").build());
        setUpUser2 = userRepository.save(
            User.builder().nickname("참여자1").email("part@gmail.com").birth(
                LocalDate.EPOCH).gender(Gender.FEMALE).temperature(42.1).provider("kakao").build());
        testMogakko = Mogakko.builder().title("title").content("제곧내").views(20).likeCount(10)
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(2)).deadline(LocalDateTime.now().plusHours(1))
            .maxParticipants(10).creator(setUpUser1).build();

        mogakkoRepository.save(testMogakko);
        tagRepository.findAll().forEach(tag -> {
            if (tag.getId() % 2 == 0) { // id가 짝수인 것만 태그로.
                mogakkoTagRepository.save(MogakkoTag.builder()
                    .tag(tag).mogakko(testMogakko).build());
            }
        });
        participantRepository.save(
            Participant.builder().user(setUpUser2).mogakko(testMogakko).build());

        MogakkoLocation testMogakkoLocation = MogakkoLocation.builder().address("테스트 주소~").latitude(10.31232)
            .longitude(105.4279823801).city("심곡본동").mogakko(testMogakko).build();
        mogakkoLocationRepository.save(testMogakkoLocation);

        tagIds.addAll(List.of(js.getId(), python.getId(), codingTest.getId(), backend.getId()));
    }

    @AfterAll
    void tearDown() {
        chatMessageRepository.deleteAll();
        participantRepository.deleteAll();
        mogakkoTagRepository.deleteAll();
        mogakkoLocationRepository.deleteAll();
        chatParticipantRepository.deleteAll();
        chatRoomRepository.deleteAll();
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
            User.builder().nickname("생성자").email("cho@gmail.com").birth(
                LocalDate.EPOCH).gender(Gender.MALE).temperature(36.5).provider("github").build());

        LocalDateTime startTime = LocalDateTime.now();
        MogakkoCreateRequestDto mogakkoCreateRequestDto = new MogakkoCreateRequestDto(
            savedCreator.getId(),
            "제목",
            new LocationInfoDto("주소", 20.4892, 125.2387342, "개봉동"),
            startTime,
            startTime.plusHours(2),
            startTime.plusHours(1),
            null,
            "내용",
            List.of(tagIds.get(0), tagIds.get(1), tagIds.get(2)));

        // when
        MogakkoCreateResponseDto responseDto = mogakkoService.save(mogakkoCreateRequestDto);

        // then
        Optional<Mogakko> mogakkoOptional = mogakkoRepository.findByIdAndDeletedAtIsNull(
            responseDto.id());
        assertThat(mogakkoOptional).isPresent();

        Mogakko createdMogakko = mogakkoOptional.get();
        assertThat(createdMogakko.getTitle()).isEqualTo("제목");
        assertThat(createdMogakko.getMaxParticipants()).isEqualTo(Mogakko.DEFAULT_MAX_PARTICIPANTS);
        assertThat(createdMogakko.getViews()).isZero();
        assertThat(createdMogakko.getCreator().getId()).isEqualTo(savedCreator.getId());
        assertThat(createdMogakko.getChatRoom()).isNotNull();

        assertThat(mogakkoTagRepository.findAllByMogakko(createdMogakko)).hasSize(3);
    }

    @Test
    @DisplayName("id 값으로 특정 모각코 디테일 정보를 가져올 수 있다")
    void success_find_mogakko_info_in_detail() {
        // when
        MogakkoDetailResponseDto responseDto = mogakkoService.findDetail(testMogakko.getId());

        // then
        assertThat(responseDto.creatorInfo()).isNotNull();
        assertThat(responseDto.creatorInfo().nickname()).isEqualTo("생성자");
        assertThat(responseDto.participants()).hasSize(1);
        assertThat(responseDto.mogakkoInfo().title()).isEqualTo(testMogakko.getTitle());
    }

    @Test
    @DisplayName("모각코 정보를 업데이트 할 수 있다")
    @Transactional
    void success_update_mogakko_info_and_tags() {
        // given
        String updateTitle = "바뀐 제목";
        LocationInfoDto updateLocation = new LocationInfoDto("바뀐 주소", 25.12, 110.2489, "구로동");
        String updateContent = "바뀐 내용";

        MogakkoUpdateRequestDto requestDto = new MogakkoUpdateRequestDto(
            setUpUser1.getId(), updateTitle, updateLocation,
            testMogakko.getStartTime(), testMogakko.getStartTime().plusHours(5),
            testMogakko.getDeadline(),
            testMogakko.getMaxParticipants(), updateContent, new ArrayList<>(tagIds));

        // when
        MogakkoUpdateResponseDto responseDto = mogakkoService.update(requestDto, testMogakko.getId());

        // then
        assertThat(responseDto.id()).isEqualTo(testMogakko.getId());

        Optional<Mogakko> mogakkoOptional = mogakkoRepository.findById(testMogakko.getId());
        assertThat(mogakkoOptional).isPresent();

        Mogakko updatedMogakko = mogakkoOptional.get();
        List<MogakkoTag> updatedMogakkoTags = mogakkoTagRepository.findAllByMogakko(updatedMogakko);

        assertThat(updatedMogakko.getTitle()).isEqualTo(updateTitle);
        assertThat(updatedMogakko.getContent()).isEqualTo(updateContent);
        assertThat(updatedMogakko.getEndTime().truncatedTo(ChronoUnit.MILLIS))
            .isEqualTo(testMogakko.getStartTime().plusHours(5).truncatedTo(ChronoUnit.MILLIS));
        assertThat(updatedMogakkoTags).hasSize(tagIds.size());

        Optional<MogakkoLocation> locationOptional = mogakkoLocationRepository.findByMogakko(updatedMogakko);
        assertThat(locationOptional).isPresent();
        MogakkoLocation updatedMogakkoLocation = locationOptional.get();
        assertThat(updatedMogakkoLocation.getAddress()).isEqualTo(updateLocation.address());
        assertThat(updatedMogakkoLocation.getCity()).isEqualTo(updateLocation.city());
        assertThat(updatedMogakkoLocation.getLatitude()).isEqualTo(updateLocation.latitude());
        assertThat(updatedMogakkoLocation.getLongitude()).isEqualTo(updateLocation.longitude());
    }

    @Test
    @DisplayName("모각코 동시 조회 시 조회 수가 제대로 오르는지 확인한다.")
    void success_increasing_mogakko_views_at_the_same_time() throws InterruptedException {
        // given
        long initialViews = mogakkoRepository.findById(testMogakko.getId()).get().getViews();
        int loop = 4;
        ThreadPoolExecutor executor = ExecutorServiceUtil.newThreadPoolExecutor();
        CountDownLatch countDownLatch = new CountDownLatch(loop);

        // when
        IntStream.range(0, loop).forEach(i -> executor.execute(() -> {
            mogakkoService.findDetail(testMogakko.getId());
            countDownLatch.countDown();
        }));

        countDownLatch.await(2000, TimeUnit.MILLISECONDS);

        // then
        Mogakko mogakko = mogakkoRepository.findById(testMogakko.getId()).get();
        assertThat(mogakko.getViews()).isEqualTo(initialViews + loop);
    }

    @Test
    @DisplayName("입력된 필터링 인자들에 대해 정상적으로 전체 검색을 수행한다")
    void success_find_all_by_filter_given_normal_args() {
        // given
        String normalSearchVal = "제곧";
        String abnormalSearchVal = "noContent";
        SearchType searchType = SearchType.TOTAL;
        List<Long> havingTagIds = mogakkoTagRepository.findAllByMogakko(testMogakko).stream()
            .map(mt -> mt.getTag().getId()).toList();

        // when
        List<MogakkoSimpleInfoResponseDto> filtered = mogakkoService.findAllByFilter(havingTagIds,
            CURSOR, normalSearchVal, searchType, PAGE_SIZE);
        List<MogakkoSimpleInfoResponseDto> filteredWithoutTagIds = mogakkoService.findAllByFilter(Collections.emptyList(),
            CURSOR, normalSearchVal, searchType, PAGE_SIZE);
        List<MogakkoSimpleInfoResponseDto> emptyFiltered = mogakkoService.findAllByFilter(Collections.emptyList(),
            CURSOR, abnormalSearchVal, searchType, PAGE_SIZE);

        // then
        assertThat(filtered).hasSize(1);
        assertThat(filtered.get(0).title()).isEqualTo(testMogakko.getTitle());
        assertThat(filteredWithoutTagIds).hasSize(1);
        assertThat(filteredWithoutTagIds.get(0).title()).isEqualTo(testMogakko.getTitle());
        assertThat(emptyFiltered).isEmpty();
    }

    @Test
    @DisplayName("모각코를 삭제할 수 있다")
    void success_delete_given_normal_id() {
        // given
        LocalDateTime startTime = LocalDateTime.now();
        MogakkoCreateRequestDto createRequestDto = new MogakkoCreateRequestDto(
            setUpUser2.getId(),
            "곧 삭제될 모각코",
            new LocationInfoDto("주소1", 10.4892, 115.2387342, "가리봉동"),
            startTime,
            startTime.plusHours(2),
            startTime.plusHours(1),
            null,
            "내용1",
            Collections.emptyList());
        MogakkoCreateResponseDto saved = mogakkoService.save(createRequestDto);

        // when
        Long id = saved.id();
        mogakkoService.delete(id);

        // then
        assertThatThrownBy(() -> mogakkoService.getByIdNotDeleted(id))
            .isInstanceOf(MogakkoException.class)
            .hasFieldOrPropertyWithValue("errorType", MogakkoErrorType.NOT_FOUND);
    }

    @Test
    @DisplayName("최소 검색 문자 수보다 작은 문자 수로 검색할 수 없다")
    void fail_find_all_by_filter_given_search_value_less_than_minimum() {
        // given
        String search = "제";

        // when, then
        assertThatThrownBy(
            () -> mogakkoService.findAllByFilter(null, Long.MAX_VALUE, search, SearchType.TOTAL,
                10))
            .isInstanceOf(MogakkoException.class)
            .hasFieldOrPropertyWithValue("errorType", MogakkoErrorType.TOO_LITTLE_INPUT);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 또는 삭제된 유저는 모각코를 생성할 수 없다")
    @Transactional
    void fail_create_mogakko_given_deleted_or_non_existent_user() {
        // given
        Long nonExistentId = Long.MAX_VALUE;
        setUpUser2.delete();
        userRepository.save(setUpUser2);

        LocalDateTime startTime = LocalDateTime.now();
        MogakkoCreateRequestDto deletedUserCreateRequestDto = new MogakkoCreateRequestDto(
            setUpUser2.getId(),
            "제목1",
            new LocationInfoDto("주소1", 10.4892, 115.2387342, "가리봉동"),
            startTime,
            startTime.plusHours(2),
            startTime.plusHours(1),
            null,
            "내용1",
            Collections.emptyList());
        MogakkoCreateRequestDto nonExistentUserCreateRequestDto = new MogakkoCreateRequestDto(
            nonExistentId,
            "제목2",
            new LocationInfoDto("주소2", 40.492, 117.238, "동동이"),
            startTime,
            startTime.plusHours(2),
            startTime.plusHours(1),
            null,
            "내용1",
            List.of(tagIds.get(2)));

        // when, then
        assertThatThrownBy(() -> mogakkoService.save(deletedUserCreateRequestDto))
            .isInstanceOf(UserException.class);
        assertThatThrownBy(() -> mogakkoService.save(nonExistentUserCreateRequestDto))
            .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("생성자가 아닌 유저는 모각코를 수정할 수 없다")
    void fail_update_mogakko_given_not_same_creator() {
        // given
        String updateTitle = "바뀐 제목";
        LocationInfoDto updateLocation = new LocationInfoDto("바뀐 주소", 25.12, 110.2489, "구로동");

        MogakkoUpdateRequestDto requestDto = new MogakkoUpdateRequestDto(
            setUpUser2.getId(), updateTitle, updateLocation,
            testMogakko.getStartTime(), testMogakko.getStartTime().plusHours(5),
            testMogakko.getDeadline(),
            testMogakko.getMaxParticipants(), testMogakko.getContent(), new ArrayList<>(tagIds));

        // when, then
        Long id = testMogakko.getId();
        assertThatThrownBy(() -> mogakkoService.update(requestDto, id)).isInstanceOf(
            MogakkoException.class).hasFieldOrPropertyWithValue("errorType", MogakkoErrorType.PROCESS_FORBIDDEN);
    }

    @Test
    @DisplayName("장소 기반 검색으로 모각코를 가져올 수 있다.")
    void success_find_all_by_filter_given_location() {
        // given
        String normalSearchVal = "심곡본";
        String abnormalSearchVal = "심본";
        SearchType searchType = SearchType.LOCATION;
        List<Long> havingTagIds = mogakkoTagRepository.findAllByMogakko(testMogakko).stream()
            .map(mt -> mt.getTag().getId()).toList();

        // when
        List<MogakkoSimpleInfoResponseDto> normalResult = mogakkoService.findAllByFilter(
            havingTagIds, CURSOR, normalSearchVal, searchType, PAGE_SIZE);
        List<MogakkoSimpleInfoResponseDto> abnormalResult = mogakkoService.findAllByFilter(
            havingTagIds, CURSOR, abnormalSearchVal, searchType, PAGE_SIZE);

        // then
        assertThat(normalResult).hasSize(1);
        assertThat(abnormalResult).isEmpty();
    }
}
