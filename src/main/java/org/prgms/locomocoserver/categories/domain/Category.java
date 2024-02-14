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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.global.common.InputType;
import org.prgms.locomocoserver.tags.domain.Tag;

@Entity
@Getter
@Builder
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

    @Enumerated(EnumType.STRING)
    @Column(name = "input_type", nullable = false)
    private InputType categoryInputType;

    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();

    public Category(Long id, String name, CategoryType categoryType, InputType categoryInputType, List<Tag> tags) {
        this.id = id;
        this.name = name;
        this.categoryType = categoryType;
        this.categoryInputType = categoryInputType;
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        tag.updateCategory(this);
    }
}
