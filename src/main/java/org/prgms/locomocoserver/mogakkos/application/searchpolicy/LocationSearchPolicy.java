package org.prgms.locomocoserver.mogakkos.application.searchpolicy;

import java.time.LocalDateTime;
import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTagRepository;

public class LocationSearchPolicy implements SearchPolicy {

    private final MogakkoRepository mogakkoRepository;
    private final MogakkoTagRepository mogakkoTagRepository;

    public LocationSearchPolicy(MogakkoRepository mogakkoRepository, MogakkoTagRepository mogakkoTagRepository) {
        this.mogakkoRepository = mogakkoRepository;
        this.mogakkoTagRepository = mogakkoTagRepository;
    }

    @Override
    public List<Mogakko> search(Long cursor, String searchVal, int pageSize) {
        return mogakkoRepository.findAllByCity(cursor, searchVal, pageSize, LocalDateTime.now());
    }

    @Override
    public List<Mogakko> search(Long cursor, String searchVal, List<Long> tagIds, int pageSize) {
        List<Long> filteredMogakkoIds = mogakkoTagRepository.findAllIdsByCity(tagIds, tagIds.size(), cursor, searchVal, pageSize, LocalDateTime.now());

        return mogakkoRepository.findAllById(filteredMogakkoIds);
    }
}
