package org.prgms.locomocoserver.mogakkos.domain.mogakkotags;

import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MogakkoTagRepository extends JpaRepository<MogakkoTag, Long> {

    @Query("SELECT mt FROM MogakkoTag mt WHERE mt.mogakko = :mogakko")
    List<MogakkoTag> findAllByMogakko(Mogakko mogakko);
    @Query(value = "SELECT mt.mogakko_id FROM mogakko_tags mt WHERE (mt.mogakko_id > :cursor AND mt.tag_id IN :tagIds) GROUP BY mt.mogakko_id HAVING COUNT(mt.mogakko_id) = :tagSize LIMIT 20", nativeQuery = true)
    List<Long> findAllIdsByTagIds(Iterable<Long> tagIds, int tagSize, Long cursor);
    void deleteByTagAndMogakko(Tag tag, Mogakko mogakko);
}
