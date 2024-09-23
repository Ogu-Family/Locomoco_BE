package org.prgms.locomocoserver.mogakkos.dto.request;

import java.time.LocalDateTime;

public record SearchConditionDto(LocalDateTime searchTime,
                                 long offset,
                                 int pageSize) {

}
