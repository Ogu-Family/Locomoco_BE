package org.prgms.locomocoserver.tags.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.categories.domain.Category;

@Entity
@Getter
@Table(name = "tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public Tag(Long id, String name, Category category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public void updateCategory(Category category) {
        if (Objects.nonNull(category)) {
            category.getTags().remove(this);
        }

        category.getTags().add(this);
        this.category = category;
    }
}
