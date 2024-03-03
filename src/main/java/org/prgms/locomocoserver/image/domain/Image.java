package org.prgms.locomocoserver.image.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.global.common.BaseEntity;

@Getter
@Entity
@Table(name = "images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key")
    private String key;

    @Column(name = "path")
    private String path;

    @Builder
    public Image(String key, String path) {
        this.key = key;
        this.path = path;
    }
}
