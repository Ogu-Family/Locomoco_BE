package org.prgms.locomocoserver.mogakkos.domain.mogakkotags;

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
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.tags.domain.Tag;

@Entity
@Getter
@Builder
@Table(name = "mogakko_tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MogakkoTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogakko_id", nullable = false)
    private Mogakko mogakko;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    public MogakkoTag(Long id, Mogakko mogakko, Tag tag) {
        this.id = id;
        this.mogakko = mogakko;
        this.tag = tag;
    }

    public void updateMogakko(Mogakko mogakko) {
        if (Objects.nonNull(mogakko)) {
            mogakko.getMogakkoTags().remove(this);
        }

        mogakko.getMogakkoTags().add(this);
        this.mogakko = mogakko;
    }
}
