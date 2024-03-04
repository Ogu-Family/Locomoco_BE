package org.prgms.locomocoserver.mogakkos.application.searchpolicy;

import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;

public interface SearchPolicy {
    List<Mogakko> search(Long cursor, String searchVal);
    List<Mogakko> search(Long cursor, String searchVal, List<Long> tagIds);
}
