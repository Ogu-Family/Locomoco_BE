package org.prgms.locomocoserver.categories.presentation;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.categories.application.CategoryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class CategoryController {

    private final CategoryService categoryService;
}
