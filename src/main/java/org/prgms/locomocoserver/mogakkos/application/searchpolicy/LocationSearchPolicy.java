package org.prgms.locomocoserver.mogakkos.application.searchpolicy;

import java.time.LocalDateTime;
import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.dto.CursorDto;
import org.springframework.stereotype.Component;

@Component
public class LocationSearchPolicy implements SearchPolicy {

    private final MogakkoRepository mogakkoRepository;

    public LocationSearchPolicy(MogakkoRepository mogakkoRepository) {
        this.mogakkoRepository = mogakkoRepository;
    }

    @Override
    public List<Mogakko> search(String searchVal, List<Long> tagIds, int pageSize,
        LocalDateTime searchTime, Long offset) {

        return mogakkoRepository.findAllByCity(tagIds, searchVal, pageSize, searchTime, offset);
    }
}
