package org.prgms.locomocoserver.mogakkos.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.prgms.locomocoserver.categories.domain.Category;
import org.prgms.locomocoserver.categories.domain.CategoryRepository;
import org.prgms.locomocoserver.categories.domain.CategoryType;
import org.prgms.locomocoserver.mogakkos.domain.MGCType;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTagRepository;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.request.SelectedTagsDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoCreateResponseDto;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.tags.domain.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class MogakkoServiceTest {

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

    @BeforeAll
    void setUp() {
        Category langs = Category.builder().categoryType(CategoryType.MOGAKKO).name("개발 언어").build();
        Category coding = Category.builder().categoryType(CategoryType.MOGAKKO).name("개발 유형").build();

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
    }

    @Test
    @DisplayName("모각코 생성이 제대로 되는지 확인한다")
    void success_create_mogakko() {
        // given
        LocalDateTime startTime = LocalDateTime.now();
        MogakkoCreateRequestDto mogakkoCreateRequestDto = new MogakkoCreateRequestDto("제목",
            "장소",
            MGCType.LOCATION,
            startTime,
            startTime.plusHours(2),
            startTime.plusHours(1),
            null,
            "내용",
            List.of(new SelectedTagsDto(1L, List.of(1L)),
                new SelectedTagsDto(2L, List.of(3L, 4L))));

        // when
        MogakkoCreateResponseDto responseDto = mogakkoService.save(mogakkoCreateRequestDto);

        // then
        Optional<Mogakko> mogakkoOptional = mogakkoRepository.findByIdAndDeletedAtIsNull(responseDto.id());
        assertThat(mogakkoOptional.isPresent()).isTrue();

        Mogakko createdMogakko = mogakkoOptional.get();
        assertThat(createdMogakko.getTitle()).isEqualTo("제목");

        assertThat(mogakkoTagRepository.findAll()).hasSize(3);
    }
}
