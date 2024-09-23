package org.prgms.locomocoserver.mogakkos.application.searchpolicy;

import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoFilterRepository;
import org.prgms.locomocoserver.mogakkos.dto.request.SearchConditionDto;
import org.prgms.locomocoserver.mogakkos.dto.request.SearchParameterDto;
import org.springframework.stereotype.Component;

@Component
public class TotalSearchPolicy implements SearchPolicy {

    private final MogakkoFilterRepository mogakkoFilterRepository;

    public TotalSearchPolicy(MogakkoFilterRepository mogakkoFilterRepository) {
        this.mogakkoFilterRepository = mogakkoFilterRepository;
    }

    @Override
    public List<Mogakko> search(SearchParameterDto searchParameterDto, SearchConditionDto searchConditionDto) {

        return mogakkoFilterRepository.findAll(searchParameterDto, searchConditionDto);
    }
}
