package org.prgms.locomocoserver.mogakkos.domain.mogakkotags;

import java.time.LocalDateTime;
import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MogakkoTagRepository extends JpaRepository<MogakkoTag, Long> {

    @Query("SELECT mt FROM MogakkoTag mt WHERE mt.mogakko = :mogakko")
    List<MogakkoTag> findAllByMogakko(Mogakko mogakko);
    @Query(value = "SELECT mt.mogakko_id "
        + "FROM mogakko_tags mt "
        + "INNER JOIN mogakko m ON m.id > :cursor AND m.deadline > :now AND m.deleted_at IS NULL AND m.id = mt.mogakko_id "
        + "INNER JOIN locations l ON l.mogakko_id = m.id AND l.city LIKE :city% "
        + "WHERE mt.tag_id IN :tagIds "
        + "GROUP BY mt.mogakko_id HAVING COUNT(mt.mogakko_id) = :tagSize "
        + "ORDER BY mt.mogakko_id "
        + "LIMIT :pageSize", nativeQuery = true)
    List<Long> findAllIdsByCity(Iterable<Long> tagIds, int tagSize, Long cursor, String city, int pageSize, LocalDateTime now);
    void deleteByTagAndMogakko(Tag tag, Mogakko mogakko);
}
