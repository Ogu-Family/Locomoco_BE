package org.prgms.locomocoserver.mogakkos.application.searchpolicy;

import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;

public class TotalSearchPolicy implements SearchPolicy {

    private final MogakkoRepository mogakkoRepository;

    public TotalSearchPolicy(MogakkoRepository mogakkoRepository) {
        this.mogakkoRepository = mogakkoRepository;
    }

    @Override
    public List<Mogakko> search(Long cursor, String searchVal) {
        return mogakkoRepository.findAllByFilter(cursor, searchVal);
    }

    @Override
    public List<Mogakko> search(Long cursor, String searchVal, List<Long> tagIds) {
        return mogakkoRepository.findAllByFilter(cursor, searchVal, tagIds, tagIds.size());
    }
}
