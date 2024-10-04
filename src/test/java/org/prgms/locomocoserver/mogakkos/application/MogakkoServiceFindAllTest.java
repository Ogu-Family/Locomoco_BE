package org.prgms.locomocoserver.mogakkos.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.categories.domain.Category;
import org.prgms.locomocoserver.categories.domain.CategoryInputType;
import org.prgms.locomocoserver.categories.domain.CategoryRepository;
import org.prgms.locomocoserver.categories.domain.CategoryType;
import org.prgms.locomocoserver.image.domain.Image;
import org.prgms.locomocoserver.image.domain.ImageRepository;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocation;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocationRepository;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTag;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTagRepository;
import org.prgms.locomocoserver.mogakkos.domain.vo.AddressInfo;
import org.prgms.locomocoserver.mogakkos.dto.request.SearchConditionDto;
import org.prgms.locomocoserver.mogakkos.dto.request.SearchParameterDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoSimpleInfoResponseDto;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.tags.domain.TagRepository;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.prgms.locomocoserver.global.TestFactory.*;

@SpringBootTest
class MogakkoServiceFindAllTest {
    private static final int PAGE_SIZE = 10;
    private static final int OFFSET = 0;
    private static final LocalDateTime SEARCH_TIME = LocalDateTime.now();

    @Autowired
    private MogakkoService mogakkoService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MogakkoRepository mogakkoRepository;
    @Autowired
    private MogakkoLocationRepository mogakkoLocationRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TagRepository tagRepository;

    private User creator;
    private List<MogakkoLocation> testLocations;
    private List<Tag> testTags;
    @Autowired
    private MogakkoTagRepository mogakkoTagRepository;

    @BeforeEach
    void setUp() {
        creator = createUser();
        Image image = creator.getProfileImage();
        imageRepository.save(image);
        userRepository.save(creator);

        AddressInfo addressInfo1 = AddressInfo.builder().address("주소").city("법정동").hCity("행정동").build();
        MogakkoLocation testLocation1 = MogakkoLocation.builder().longitude(12.12314d).latitude(127.23522d).addressInfo(addressInfo1).build();
        AddressInfo addressInfo2 = AddressInfo.builder().address("소주").city("동정법").hCity("행정동").build();
        MogakkoLocation testLocation2 = MogakkoLocation.builder().longitude(12.12314d).latitude(127.23522d).addressInfo(addressInfo2).build();

        testLocations = mogakkoLocationRepository.saveAll(List.of(testLocation1, testLocation2));

        Category category1 = Category.builder().name("category1").categoryType(CategoryType.MOGAKKO)
            .categoryInputType(CategoryInputType.COMBOBOX).build();
        Category category2 = Category.builder().name("category2").categoryType(CategoryType.MOGAKKO)
            .categoryInputType(CategoryInputType.COMBOBOX).build();
        categoryRepository.saveAll(List.of(category1, category2));

        testTags = List.of(Tag.builder().category(category1).name("tag1").build(),
            Tag.builder().category(category1).name("tag2").build(),
            Tag.builder().category(category2).name("tag3").build());
        tagRepository.saveAll(testTags);
    }

    @AfterEach
    void tearDown() {
        mogakkoTagRepository.deleteAll();
        mogakkoLocationRepository.deleteAll();
        mogakkoRepository.deleteAll();
        userRepository.deleteAll();
        imageRepository.deleteAll();
        tagRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("제목, 내용에 대한 검색을 제대로 수행할 수 있다.")
    void success_find_mogakko_filtered_by_title_and_content() throws Exception {
        // given
        Mogakko mogakko1 = createMogakko(creator);
        Mogakko mogakko2 = createMogakko(creator);

        changeMogakkoInfo(mogakko1, "모코", "");
        changeMogakkoInfo(mogakko2, "조금 더 긴 제목", "모코");

        testLocations.get(0).updateMogakko(mogakko1);
        testLocations.get(1).updateMogakko(mogakko2);

        mogakkoRepository.saveAll(List.of(mogakko1, mogakko2));
        mogakkoLocationRepository.saveAll(testLocations);

        List<String> search = List.of("제목", "모코");

        // when
        List<List<MogakkoSimpleInfoResponseDto>> dtos = search.stream().map(s ->
                mogakkoService.findAll(
                        new SearchParameterDto(s, null, null, null),
                        new SearchConditionDto(SEARCH_TIME, OFFSET, PAGE_SIZE))
                )
                .toList();

        // then
        List<MogakkoSimpleInfoResponseDto> dto1 = dtos.get(0);
        List<MogakkoSimpleInfoResponseDto> dto2 = dtos.get(1);

        assertThat(dto1).hasSize(1);
        assertThat(dto2).hasSize(2);
    }

    @Test
    @DisplayName("장소에 대한 검색을 제대로 수행할 수 있다")
    void success_find_mogakko_filtered_by_locations() {
        // given
        Mogakko mogakko1 = createMogakko(creator);
        Mogakko mogakko2 = createMogakko(creator);

        testLocations.get(0).updateMogakko(mogakko1);
        testLocations.get(1).updateMogakko(mogakko2);
        mogakkoRepository.saveAll(List.of(mogakko1, mogakko2));
        mogakkoLocationRepository.saveAll(testLocations);

        List<String> search = List.of(
                testLocations.get(0).getAddressInfo().getAddress(),
                testLocations.get(1).getAddressInfo().getCity(),
                testLocations.get(0).getAddressInfo().getHCity(),
                "아무 검색 데이터");

        // when
        List<List<MogakkoSimpleInfoResponseDto>> dtos = search.stream().map(
            s -> mogakkoService.findAll(new SearchParameterDto(null, s, null, null),
                new SearchConditionDto(SEARCH_TIME, OFFSET, PAGE_SIZE))).toList();

        // then
        List<Integer> expectedSize = List.of(1, 1, 2, 0);

        for (int idx = 0; idx < dtos.size(); idx++) {
            assertThat(dtos.get(idx)).hasSize(expectedSize.get(idx));
        }
    }

    @Test
    @DisplayName("유저 닉네임에 대한 검색을 제대로 수행할 수 있다")
    void success_find_mogakko_filtered_by_user_nickname() {
        // given
        Mogakko mogakko1 = createMogakko(creator);
        Mogakko mogakko2 = createMogakko(creator);

        mogakkoRepository.saveAll(List.of(mogakko1, mogakko2));

        // when
        List<String> search = List.of(creator.getNickname(), "아무 검색어");

        List<List<MogakkoSimpleInfoResponseDto>> dtos = search.stream().map(
            s -> mogakkoService.findAll(new SearchParameterDto(null, null, s, null),
                new SearchConditionDto(SEARCH_TIME, OFFSET, PAGE_SIZE))).toList();

        // then
        List<Integer> expectedSize = List.of(2, 0);

        for (int idx = 0; idx < dtos.size(); idx++) {
            assertThat(dtos.get(idx)).hasSize(expectedSize.get(idx));
        }
    }

    @Test
    @DisplayName("태그 필터링에 대한 검색을 제대로 수행할 수 있다")
    void success_find_mogakko_filtered_by_tags() { // m1 - t1 - c1, m1 - t3 - c2, m2 - t2 - c1
        // given
        Mogakko mogakko1 = createMogakko(creator);
        Mogakko mogakko2 = createMogakko(creator);
        mogakkoRepository.saveAll(List.of(mogakko1, mogakko2));

        List<MogakkoTag> mogakkoTags = List.of(
            MogakkoTag.builder().tag(testTags.get(0)).mogakko(mogakko1).build(),
            MogakkoTag.builder().tag(testTags.get(1)).mogakko(mogakko2).build(),
            MogakkoTag.builder().tag(testTags.get(2)).mogakko(mogakko1).build());
        mogakkoTagRepository.saveAll(mogakkoTags);

        List<List<Long>> search = List.of(
            List.of(testTags.get(0).getId()),
            List.of(testTags.get(0).getId(), testTags.get(1).getId()),
            List.of(testTags.get(0).getId(), testTags.get(2).getId()),
            List.of(testTags.get(0).getId(), testTags.get(1).getId(), testTags.get(2).getId()),
            List.of());

        // when
        List<List<MogakkoSimpleInfoResponseDto>> dtos = search.stream().map(
            s -> mogakkoService.findAll(new SearchParameterDto(null, null, null, s),
                new SearchConditionDto(SEARCH_TIME, OFFSET, PAGE_SIZE))).toList();

        // then
        List<Integer> expectedSize = List.of(1, 2, 1, 1, 2);

        for (int idx = 0; idx < dtos.size(); idx++) {
            assertThat(dtos.get(idx)).hasSize(expectedSize.get(idx));
        }
    }

    @Test
    @DisplayName("복합 조건에 대한 검색을 제대로 수행할 수 있다")
    void success_find_mogakko_filtered_by_mixed_parameter() throws Exception {
        // given
        Mogakko mogakko1 = createMogakko(creator);
        Mogakko mogakko2 = createMogakko(creator);
        Mogakko mogakko3 = createMogakko(creator);
        Mogakko mogakko4 = createMogakko(creator);

        changeMogakkoInfo(mogakko1, "한 음 절 로 이 루 어 진 모 각 코", "");
        changeMogakkoInfo(mogakko2, "모각코 각코", null);
        changeMogakkoInfo(mogakko4, "모각코 모여", "안녕");

        mogakkoRepository.saveAll(List.of(mogakko1, mogakko2, mogakko3, mogakko4));

        testLocations.get(1).updateMogakko(mogakko2);
        testLocations.get(0).updateMogakko(mogakko4);
        mogakkoLocationRepository.saveAll(testLocations);

        List<MogakkoTag> mogakkoTags = List.of(
            MogakkoTag.builder().mogakko(mogakko1).tag(testTags.get(0)).build(),
            MogakkoTag.builder().mogakko(mogakko1).tag(testTags.get(2)).build(),
            MogakkoTag.builder().mogakko(mogakko2).tag(testTags.get(0)).build(),
            MogakkoTag.builder().mogakko(mogakko3).tag(testTags.get(0)).build()
        );
        mogakkoTagRepository.saveAll(mogakkoTags);

        List<SearchParameterDto> searchParameterDtos = List.of(
            new SearchParameterDto("모각코", testLocations.get(1).getAddressInfo().getCity(), null,
                null),
            new SearchParameterDto("", testLocations.get(0).getAddressInfo().getHCity(),
                creator.getNickname(), null),
            new SearchParameterDto(null, "", creator.getNickname(),
                List.of(testTags.get(0).getId(), testTags.get(1).getId())),
            new SearchParameterDto("모각코", testLocations.get(0).getAddressInfo().getAddress(), "",
                List.of(testTags.get(0).getId())),
            new SearchParameterDto("안녕", "", "", List.of()),
            new SearchParameterDto("안녕", "", "", List.of(testTags.get(0).getId())));

        // when
        List<List<MogakkoSimpleInfoResponseDto>> resDtos = searchParameterDtos.stream().map(
            spd -> mogakkoService.findAll(spd,
                new SearchConditionDto(SEARCH_TIME, OFFSET, PAGE_SIZE))).toList();

        // then
        List<Integer> expectedSize = List.of(1, 2, 3, 0, 1, 0);
        for (int idx = 0; idx < resDtos.size(); idx++) {
            assertThat(resDtos.get(idx)).hasSize(expectedSize.get(idx));
        }
    }

    private void changeMogakkoInfo(Mogakko mogakko, String title, String content) throws Exception {
        Field titleField = mogakko.getClass().getDeclaredField("title");
        Field contentField = mogakko.getClass().getDeclaredField("content");
        titleField.setAccessible(true);
        contentField.setAccessible(true);

        if (title != null && !title.isBlank())
            titleField.set(mogakko, title);

        if (content != null)
            contentField.set(mogakko, content);
    }
}
