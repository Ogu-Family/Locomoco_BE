package org.prgms.locomocoserver.mogakkos.application.searchpolicy;

import java.util.List;
import org.prgms.locomocoserver.location.domain.LocationRepository;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTagRepository;

public class LocationSearchPolicy implements SearchPolicy {

    private final MogakkoRepository mogakkoRepository;
    private final LocationRepository locationRepository;
    private final MogakkoTagRepository mogakkoTagRepository;

    public LocationSearchPolicy(MogakkoRepository mogakkoRepository,
        LocationRepository locationRepository, MogakkoTagRepository mogakkoTagRepository) {
        this.mogakkoRepository = mogakkoRepository;
        this.locationRepository = locationRepository;
        this.mogakkoTagRepository = mogakkoTagRepository;
    }

    @Override
    public List<Mogakko> search(Long cursor, String searchVal) {
        return mogakkoRepository.findAllByCity(cursor, searchVal);
    }

    @Override
    public List<Mogakko> search(Long cursor, String searchVal, List<Long> tagIds) {
        List<Long> filteredMogakkoIds = mogakkoTagRepository.findAllIdsByCity(tagIds, tagIds.size(), cursor, searchVal);

        return mogakkoRepository.findAllById(filteredMogakkoIds);
    }
}
