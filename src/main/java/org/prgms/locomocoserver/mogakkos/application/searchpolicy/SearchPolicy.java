package org.prgms.locomocoserver.mogakkos.application.searchpolicy;

import java.time.LocalDateTime;
import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.dto.CursorDto;

public interface SearchPolicy {
    List<Mogakko> search(String searchVal, List<Long> tagIds, int pageSize, LocalDateTime searchTime, CursorDto cursorDto);
}
