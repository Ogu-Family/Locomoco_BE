package org.prgms.locomocoserver.mogakkos.dto.request;

import java.util.List;

public record MogakkoFilterRequestDto(String address,
                                      List<Long> tags) {

}
