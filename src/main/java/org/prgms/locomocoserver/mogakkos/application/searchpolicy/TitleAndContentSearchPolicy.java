package org.prgms.locomocoserver.mogakkos.application.searchpolicy;

import java.time.LocalDateTime;
import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.dto.CursorDto;
import org.springframework.stereotype.Component;

@Component
public class TitleAndContentSearchPolicy implements SearchPolicy {

    private final MogakkoRepository mogakkoRepository;

    public TitleAndContentSearchPolicy(MogakkoRepository mogakkoRepository) {
        this.mogakkoRepository = mogakkoRepository;
    }

    @Override
    public List<Mogakko> search(String searchVal, List<Long> tagIds, int pageSize, LocalDateTime searchTime, CursorDto cursorDto) {

        return mogakkoRepository.findAllByTitleAndContent(searchVal, tagIds, pageSize, searchTime,
            cursorDto.countCursor(), cursorDto.timeCursor(), cursorDto.idCursor());
    }
}
