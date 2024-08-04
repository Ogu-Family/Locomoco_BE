package org.prgms.locomocoserver.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void delete() {
        if (isDeleted()) throw new IllegalArgumentException("이미 삭제된 엔티티 입니다.");
        this.deletedAt = LocalDateTime.now();
    }

    public void updateUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
