package org.prgms.locomocoserver.categories.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT c FROM Category c INNER JOIN FETCH c.tags WHERE c.categoryType = :categoryType")
    List<Category> findAllByType(CategoryType categoryType);
}
