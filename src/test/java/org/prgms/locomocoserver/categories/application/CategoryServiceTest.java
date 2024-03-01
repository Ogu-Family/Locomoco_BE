package org.prgms.locomocoserver.categories.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.categories.domain.Category;
import org.prgms.locomocoserver.categories.domain.CategoryInputType;
import org.prgms.locomocoserver.categories.domain.CategoryRepository;
import org.prgms.locomocoserver.categories.domain.CategoryType;
import org.prgms.locomocoserver.categories.dto.response.CategoriesWithTagsDto;
import org.prgms.locomocoserver.global.common.dto.Results;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.tags.domain.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
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
    }

    @AfterEach
    void tearDown() {
        tagRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("모각코 생성에 필요한 카테고리와 태그들을 제대로 가져올 수 있다.")
    void success_find_all_categories_and_tags_by_mogakko_type() {
        // when
        Results<CategoriesWithTagsDto> results = categoryService.findAllBy(CategoryType.MOGAKKO);

        // then
        assertThat(results.data()).hasSize(2);
        assertThat(results.data().get(0).tags()).hasSize(2);
        assertThat(results.data().get(1).tags()).hasSize(2);
    }
}
