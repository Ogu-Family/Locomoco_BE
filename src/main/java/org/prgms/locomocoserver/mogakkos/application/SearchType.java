package org.prgms.locomocoserver.mogakkos.application;

import java.util.function.Function;
import org.prgms.locomocoserver.mogakkos.application.searchpolicy.LocationSearchPolicy;
import org.prgms.locomocoserver.mogakkos.application.searchpolicy.SearchPolicy;
import org.prgms.locomocoserver.mogakkos.application.searchpolicy.TotalSearchPolicy;
import org.prgms.locomocoserver.mogakkos.dto.SearchRepositoryDto;

public enum SearchType {
    TOTAL(dto -> new TotalSearchPolicy(dto.mogakkoRepository())),
    LOCATION(dto -> new LocationSearchPolicy(dto.mogakkoRepository()));

    private final Function<SearchRepositoryDto, SearchPolicy> searchPolicy;

    SearchType(Function<SearchRepositoryDto, SearchPolicy> searchPolicy) {
        this.searchPolicy = searchPolicy;
    }

    public SearchPolicy getSearchPolicy(SearchRepositoryDto dto) {
        return searchPolicy.apply(dto);
    }
}
