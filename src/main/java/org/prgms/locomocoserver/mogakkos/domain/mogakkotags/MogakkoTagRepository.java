package org.prgms.locomocoserver.mogakkos.domain.mogakkotags;

import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MogakkoTagRepository extends JpaRepository<MogakkoTag, Long> {

    @Query("SELECT mt FROM MogakkoTag mt WHERE mt.mogakko = :mogakko")
    List<MogakkoTag> findAllByMogakko(Mogakko mogakko);
    void deleteByTag(Tag tag);
}
