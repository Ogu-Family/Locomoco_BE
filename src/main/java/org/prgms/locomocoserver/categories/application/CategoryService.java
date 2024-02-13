package org.prgms.locomocoserver.categories.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.categories.domain.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
}
