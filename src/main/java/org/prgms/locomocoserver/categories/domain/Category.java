package org.prgms.locomocoserver.categories.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.tags.domain.Tag;

@Entity
@Getter
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CategoryType categoryType;

    @OneToMany(mappedBy = "category")
    private List<Tag> tags = new ArrayList<>();

    public Category(Long id, String name, CategoryType categoryType, List<Tag> tags) {
        this.id = id;
        this.name = name;
        this.categoryType = categoryType;
        this.tags = tags;
    }

    public static CategoryBuilder builder() {
        return new CategoryBuilder();
    }

    public static class CategoryBuilder {

        private Long id;
        private String name;
        private CategoryType categoryType;
        private List<Tag> tags;
        private boolean isTagSet;

        CategoryBuilder() {
        }

        public CategoryBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CategoryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CategoryBuilder categoryType(CategoryType categoryType) {
            this.categoryType = categoryType;
            return this;
        }

        public CategoryBuilder tags(List<Tag> tags) {
            this.tags = tags;
            this.isTagSet = true;
            return this;
        }

        public Category build() {
            return new Category(this.id,
                this.name,
                this.categoryType,
                this.isTagSet ? this.tags : new ArrayList<>());
        }

        @Override
        public String toString() {
            return "CategoryBuilder{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryType=" + categoryType +
                ", tags=" + tags +
                ", isTagSet=" + isTagSet +
                '}';
        }
    }
}
