package org.prgms.locomocoserver.categories.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.categories.application.CategoryService;
import org.prgms.locomocoserver.categories.domain.CategoryType;
import org.prgms.locomocoserver.categories.dto.response.CategoriesWithTagsDto;
import org.prgms.locomocoserver.global.common.dto.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category controller", description = "카테고리 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@CrossOrigin("*")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/category")
    @Operation(summary = "카테고리 가져오기", description = "특정 타입에 관한 카테고리와 연관된 태그들을 전부 가져옵니다.")
    @ApiResponses(
        @ApiResponse(responseCode = "200", description = "카테고리 가져오기 성공")
    )
    public ResponseEntity<Results<CategoriesWithTagsDto>> findAllByType(
        @Parameter(description = "마이페이지 관련인지, 모각코 관련인지 적어주는 파라미터", example = "MOGAKKO")
        @RequestParam(name = "type") CategoryType categoryType) {
        Results<CategoriesWithTagsDto> results = categoryService.findAllBy(categoryType);

        return ResponseEntity.ok(results);
    }
}
