package org.prgms.locomocoserver.mogakkos.application.searchpolicy;

import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.dto.request.SearchConditionDto;
import org.prgms.locomocoserver.mogakkos.dto.request.SearchParameterDto;
import org.springframework.stereotype.Component;

@Component
public class LocationSearchPolicy implements SearchPolicy {

    private final MogakkoRepository mogakkoRepository;

    public LocationSearchPolicy(MogakkoRepository mogakkoRepository) {
        this.mogakkoRepository = mogakkoRepository;
    }

    @Override
    public List<Mogakko> search(SearchParameterDto searchParameterDto, SearchConditionDto searchConditionDto) {

        return mogakkoRepository.findAllByCity(searchParameterDto.tagIds(),
            searchParameterDto.totalSearch(),
            searchConditionDto.pageSize(),
            searchConditionDto.searchTime(),
            searchConditionDto.offset());
    }
}
