package org.prgms.locomocoserver.mogakkos.application.searchpolicy;

import java.time.LocalDateTime;
import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;

public class TotalSearchPolicy implements SearchPolicy {

    private final MogakkoRepository mogakkoRepository;

    public TotalSearchPolicy(MogakkoRepository mogakkoRepository) {
        this.mogakkoRepository = mogakkoRepository;
    }

    @Override
    public List<Mogakko> search(Long cursor, String searchVal, int pageSize) {
        return mogakkoRepository.findAllByFilter(cursor, searchVal, pageSize, LocalDateTime.now());
    }

    @Override
    public List<Mogakko> search(Long cursor, String searchVal, List<Long> tagIds, int pageSize) {
        return mogakkoRepository.findAllByFilter(cursor, searchVal, tagIds, tagIds.size(), pageSize, LocalDateTime.now());
    }
}
