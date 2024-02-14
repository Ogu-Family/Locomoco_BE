package org.prgms.locomocoserver.categories.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.categories.domain.Category;
import org.prgms.locomocoserver.categories.domain.CategoryRepository;
import org.prgms.locomocoserver.categories.domain.CategoryType;
import org.prgms.locomocoserver.categories.dto.response.CategoriesWithTagsDto;
import org.prgms.locomocoserver.global.common.dto.Results;
import org.prgms.locomocoserver.tags.dto.TagsInfoDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Results<CategoriesWithTagsDto> findAllBy(String type) {
        CategoryType categoryType = CategoryType.valueOf(type);

        List<Category> categories = categoryRepository.findAllByType(categoryType);

        return transformToResults(categories);
    }

    private Results<CategoriesWithTagsDto> transformToResults(List<Category> categories) {
        List<CategoriesWithTagsDto> data = categories.stream().map(
            category -> new CategoriesWithTagsDto(category.getId(), category.getName(),
                category.getCategoryInputType(),
                getTagsInfoDto(category))
        ).toList();

        return new Results<>(data);
    }

    private static List<TagsInfoDto> getTagsInfoDto(Category category) {
        return category.getTags().stream().map(tag -> new TagsInfoDto(tag.getId(), tag.getName()))
            .toList();
    }
}
